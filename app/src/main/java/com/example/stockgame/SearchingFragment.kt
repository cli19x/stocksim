package com.example.stockgame

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stock_news.StockNewsViewModel
import com.example.stock_searching.StockSearchItem
import com.example.stock_searching.StockSearchList
import com.example.stock_searching.StockSearchViewModel
import com.example.stock_searching.StockSearchingRetrofitService
import com.example.stock_watching.StockWatchingItem
import com.example.stock_watching.StockWatchingViewModel
import com.example.user.UserViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robinhood.ticker.TickerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchingFragment : Fragment(), SearchView.OnQueryTextListener  {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private var userSearchingList = ArrayList<StockSearchItem>()
    private lateinit var modelSearch: StockSearchViewModel
    private lateinit var  searchView: SearchView
    private var items: ArrayList<String> = ArrayList()
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d("submit", "=+++++++++++++++++++")
        if (!query.isNullOrEmpty()) {
            modelSearch.refreshStocks(query)
            searchView.clearFocus()
        }

        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        Log.d("change", "=+++++++++++++++++++")
        if (!newText.isNullOrEmpty()) {
            modelSearch.refreshStocks(newText)
        }
        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_searching, container, false)
        val gson = Gson()
        items = gson.fromJson(this.arguments?.getString("list"), object : TypeToken<ArrayList<String>>() {}.type)
        searchView = view.findViewById(R.id.sv_search_stock)
        searchView.setOnQueryTextListener(this)
        view.findViewById<ImageButton>(R.id.ib_seach_home).setOnClickListener {
            userSearchingList.clear()
            view.findNavController().navigate(R.id.action_searchingFragment_to_listFragment)
        }

        recyclerView = view.findViewById(R.id.rv_search_list)
        viewAdapter = RecyclerViewAdapter(userSearchingList, activity as SecondMainActivity)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = viewAdapter

        modelSearch = activity?.let { ViewModelProviders.of(it).get(StockSearchViewModel::class.java) }!!

        modelSearch.searchingStocks!!.observe(
            this,
            Observer<StockSearchList> { stocks ->
                stocks?.let {
                    Log.d("searching", "-------------------1-----------------")
                    userSearchingList.clear()
                    Log.d("searching", "--------------${stocks.bestMatches}------2----------------")
                    it.bestMatches.forEach {
                        if (!items.contains(it.symbol.toUpperCase())) {
                            userSearchingList.add(it)
                        }

                    }.also {
                        recyclerView.adapter = viewAdapter
                        viewAdapter.notifyDataSetChanged()
                    }

                    Log.d("searching", "----------3--------------------------")
                    recyclerView.adapter = viewAdapter
                    viewAdapter.notifyDataSetChanged()
                }
            })
        return view
    }





    class RecyclerViewAdapter(
        private var myDataSet: ArrayList<StockSearchItem>,
        private val activity: SecondMainActivity
    ) :
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.stock_add_item, parent, false)
            return ViewHolder(v, activity)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(myDataSet[position])
        }

        override fun getItemCount() = myDataSet.size

        class ViewHolder(private val view: View, private val activity: SecondMainActivity) : RecyclerView.ViewHolder(view) {
            fun bindItems(items: StockSearchItem) {
                val title: TextView = itemView.findViewById(R.id.tv_search_symbol)
                title.text = items.name
                val btnAdd: ImageButton = itemView.findViewById(R.id.ib_search_add)
                btnAdd.setOnClickListener {
                    val listOfHistory: ArrayList<Float> = ArrayList()
                    val gson = Gson()
                    val jsonString = gson.toJson(listOfHistory)
                    val stockItem = StockWatchingItem(items.symbol, items.name," "," ",
                        0f, 0, 0f, 0, 0,0f,
                        0, 0, 0, 0, 0f, jsonString, false)
                    (activity as SecondMainActivity).onChangeStockList(stockItem, 0)
                    Toast.makeText(activity, "Now watching ${items.symbol}", Toast.LENGTH_LONG).show()
                    itemView.findViewById<ImageButton>(R.id.ib_search_add).visibility = View.INVISIBLE
                }

            }

        }
    }
}
