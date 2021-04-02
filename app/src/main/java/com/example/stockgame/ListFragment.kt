package com.example.stockgame

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stock_watching.StockWatchingItem
import com.example.stock_watching.StockWatchingViewModel
import com.example.user.UserItem
import com.example.user.UserViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.robinhood.ticker.TickerView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.content.ContextCompat
import com.example.stock_graph.StockGraphItem
import com.example.stock_graph.StockGraphViewModel
import com.robinhood.spark.SparkView
import com.robinhood.spark.SparkAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ListFragment : Fragment() {


    private lateinit var recyclerViewOne: RecyclerView
    private lateinit var viewAdapterOne: RecyclerView.Adapter<*>
    private var userWatchingList = ArrayList<StockWatchingItem>()
    private var userOwningList = ArrayList<StockWatchingItem>()

    private lateinit var recyclerViewTwo: RecyclerView
    private lateinit var viewAdapterTwo: RecyclerView.Adapter<*>


    private lateinit var modelOne: UserViewModel
    private lateinit var modelTwo: StockWatchingViewModel
    private lateinit var modelThree: StockGraphViewModel

    private lateinit var dialog: Dialog

    private var points = ArrayList<Float>()

    private val MAX_POINTS = 60

    private var investing:Float = 0f
    private var totalPrice:Float = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerViewOne = v.findViewById(R.id.rv_list_watching)
        recyclerViewTwo = v.findViewById(R.id.rv_list_owning)
        modelOne = activity?.let { ViewModelProviders.of(it).get(UserViewModel::class.java) }!!
        modelOne.user.observe(
            this,
            Observer<UserItem> { user ->
                user?.let {
                    investing = it.investing
                    Log.d("user_list", "${it.investing}-----------------------------")
                    (v.findViewById(R.id.tv_list_ticker) as TickerView)
                        .setCharacterLists(it.investing.toString())
                    (v.findViewById(R.id.tv_list_ticker) as TickerView)
                        .text = "$${it.investing}"
                }
            }
        )
        val tickerView:TickerView = v.findViewById(R.id.tv_list_ticker)
        val sparkView = v.findViewById(R.id.sparkview_list) as SparkView
        sparkView.lineColor = ContextCompat.getColor(requireContext(), R.color.color_bg)
        sparkView.lineWidth = 12f
        modelTwo = activity?.let { ViewModelProviders.of(it).get(StockWatchingViewModel::class.java) }!!
        modelThree = activity?.let { ViewModelProviders.of(it).get(StockGraphViewModel::class.java) }!!
        modelTwo.allStocks.observe(
            viewLifecycleOwner,
            Observer<List<StockWatchingItem>> { stocks ->
                stocks?.let {
                    userWatchingList.clear()
                    userOwningList.clear()
                    var temp = 0.0f
                    totalPrice = 0f
                    it.forEach {
                        if (it.isOwning) {
                            temp += (it.lastSalePrice - it.ownPrice) * it.shares
                            totalPrice += it.ownPrice * it.shares
                            userOwningList.add(it)
                        } else {
                            userWatchingList.add(it)
                        }
                    }
//                    userOwningList.sortBy {
//                        it.symbol
//                    }
//
//                    userWatchingList.sortBy {
//                        it.symbol
//                    }
                    Log.d("list_model","------------------$investing    $temp")
                    tickerView.setCharacterLists((investing + (temp)).toString())
                    tickerView.text = "$${"%.2f".format(investing + (temp))}"
                    if (investing + (temp) >= 0) {
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formatted = current.format(formatter)
                        val temp2 = StockGraphItem(formatted, investing + (temp))
                        modelThree.insertNewPoint(temp2)
                    }

                    viewAdapterOne.notifyDataSetChanged()
                    recyclerViewOne.adapter = viewAdapterOne
                    viewAdapterTwo.notifyDataSetChanged()
                    recyclerViewTwo.adapter = viewAdapterTwo
                }
            }
        )
        modelThree.allStocks.observe(
            viewLifecycleOwner,
            Observer<List<StockGraphItem>> { data ->
                data?.let {
                    points.clear()
                    data.forEach {
                        points.add(it.data)
                    }
                    Log.d("graph","+==========${points.toString()}===================")
                    if (points.size > MAX_POINTS) {
                        modelThree.delAPoint(it[0])
                        points.removeAt(0)
                    }
                    else {
                        sparkView.adapter = MyAdapter(points.toFloatArray())
                        sparkView.adapter.notifyDataSetChanged()
                    }
                }
            }
        )
        viewAdapterOne = RecyclerViewAdapterOne(userWatchingList, investing, activity as SecondMainActivity)
        recyclerViewOne.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerViewOne.adapter = viewAdapterOne


        viewAdapterTwo = RecyclerViewAdapterTwo(userOwningList, investing, activity as SecondMainActivity)
        recyclerViewTwo.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerViewTwo.adapter = viewAdapterTwo

        val fabFund:FloatingActionButton = v.findViewById(R.id.fab_list_funds)
        val fabAdd:FloatingActionButton = v.findViewById(R.id.fab_list_add_stock)
        fabFund.setOnClickListener {
            setUpDialog()
        }

        fabAdd.setOnClickListener{
            val tempList = ArrayList<String>()
            userWatchingList.forEach {
                tempList.add(it.symbol)
            }
            userOwningList.forEach {
                tempList.add(it.symbol)
            }
            val gson = Gson()
            val jsonString = gson.toJson(tempList)
            v.findNavController().navigate(R.id.action_listFragment_to_searchingFragment,
                bundleOf("list" to jsonString))
        }

        return v
    }

    private fun setUpDialog() {
        dialog = Dialog(requireActivity())
        var dialogValue = 0
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_add_sub_fund, null)
        dialog.setContentView(view)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT , 1000)
        val editText: EditText = view.findViewById(R.id.et_add_sub)
        val fabAddSub: FloatingActionButton = view.findViewById(R.id.fab_add_sub)
        val spinner: Spinner = view.findViewById(R.id.sp_add_sub)
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                dialogValue = position
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                dialogValue = -1
            }

        }
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.fund_array,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        fabAddSub.setOnClickListener {
            Log.d("dialog","$dialogValue----------------------${editText.text}")
            if (!editText.text.isEmpty() && editText.text.toString() != "0" && editText.text.toString() != "" ) {
                if (dialogValue == 0) {
                    modelOne.changingFund(editText.text.toString().toFloat() + investing)
                    dialog.dismiss()
                }
                else if (dialogValue == 1) {
                    if (editText.text.toString().toFloat() > investing - totalPrice) {
                        Toast.makeText(activity,
                            "Cannot Withdraw Amount Larger than Buying Power!!", Toast.LENGTH_LONG).show()
                    } else {
                        modelOne.changingFund(investing - editText.text.toString().toFloat())
                        dialog.dismiss()
                    }
                }
            }

        }
        dialog.show()
    }




    class RecyclerViewAdapterOne(
        private var myDataSet: ArrayList<StockWatchingItem>,
        private var investing: Float,
        private val activity: SecondMainActivity
    ) :
        RecyclerView.Adapter<RecyclerViewAdapterOne.ViewHolder>() {


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerViewAdapterOne.ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.stock_watch_item, parent, false)
            return ViewHolder(v, activity)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(myDataSet[position])
        }

        override fun getItemCount() = myDataSet.size

        class ViewHolder(private val view: View, private val activity: SecondMainActivity) : RecyclerView.ViewHolder(view) {
            @SuppressLint("SetTextI18n")
            fun bindItems(items: StockWatchingItem) {

                val title: TextView = itemView.findViewById(R.id.tv_watch_symbol)
                title.text = items.symbol

                val name: TextView = itemView.findViewById(R.id.tv_watch_name)
                name.text = items.fullName

                val rating: TextView = itemView.findViewById(R.id.tv_watch_ticker)
                rating.text = "$${items.lastSalePrice}"
                itemView.setOnClickListener {
                    val gson = Gson()
                    val jsonString = gson.toJson(items)
                    view.findNavController().navigate(
                        R.id.action_listFragment_to_detailFragment,
                        bundleOf("stock_json" to jsonString.toString(), "type" to 1)
                    )
                }
            }

        }
    }



    class RecyclerViewAdapterTwo(
        private var myDataSet: ArrayList<StockWatchingItem>,
        private var investing: Float,
        private val activity: SecondMainActivity
    ) :
        RecyclerView.Adapter<RecyclerViewAdapterTwo.ViewHolder>() {


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerViewAdapterTwo.ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.stock_own_item, parent, false)
            return ViewHolder(v, activity)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(myDataSet[position])
        }

        override fun getItemCount() = myDataSet.size

        class ViewHolder(private val view: View, private val activity: SecondMainActivity) : RecyclerView.ViewHolder(view) {
            @SuppressLint("SetTextI18n")
            fun bindItems(items: StockWatchingItem) {

                val symbol: TextView = itemView.findViewById(R.id.tv_own_symbol)
                symbol.text = items.symbol

                val name: TextView = itemView.findViewById(R.id.tv_own_name)
                name.text = items.fullName

                val price: TextView = itemView.findViewById(R.id.tv_own_ticker)
                price.text = "$${items.lastSalePrice}"

                val share: TextView = itemView.findViewById(R.id.tv_own_shares)
                share.text = "(${items.shares} shares)"

                itemView.setOnClickListener {
                    val gson = Gson()
                    val jsonString = gson.toJson(items)
                    view.findNavController().navigate(
                        R.id.action_listFragment_to_detailFragment,
                        bundleOf("stock_json" to jsonString.toString(), "type" to 2)
                    )
                }
            }

        }
    }


    inner class MyAdapter(private val yData: FloatArray) : SparkAdapter() {

        override fun getCount(): Int {
            return yData.size
        }

        override fun getItem(index: Int): Any {
            return yData[index]
        }

        override fun getY(index: Int): Float {
            return yData[index]
        }
    }

}





