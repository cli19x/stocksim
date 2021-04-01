package com.example.stockgame

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.stock_news.NewsList
import com.example.stock_news.StockNewsItem
import com.example.stock_news.StockNewsViewModel
import com.example.stock_watching.StockWatchingItem
import com.example.stock_watching.StockWatchingViewModel
import com.example.user.UserViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import com.robinhood.ticker.TickerView
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment() {
    private lateinit var model: StockWatchingViewModel
    private lateinit var modelNews: StockNewsViewModel
    private lateinit var recyclerViewNews: RecyclerView
    private lateinit var viewAdapterNews: RecyclerView.Adapter<*>

    private lateinit var item: StockWatchingItem
    private var newsList: ArrayList<StockNewsItem> = ArrayList()

    private var priceNow = 0f
    private var type = -1
    private var points = ArrayList<Float>()

    @SuppressLint("RestrictedApi", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_detail, container, false)
        val gson = Gson()

        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    view.findNavController().navigate(R.id.action_detailFragment_to_listFragment)
                }
                return false
            }
        })
        item = gson.fromJson(this.arguments?.getString("stock_json"), StockWatchingItem::class.java)
        type = this.arguments?.getInt("type")!!
        print(type)
        (view.findViewById(R.id.tv_detail_ticker) as TickerView)
            .setCharacterLists(item.lastSalePrice.toString())
        (view.findViewById(R.id.tv_detail_ticker) as TickerView)
            .text = item.lastSalePrice.toString()

        view.findViewById<ImageButton>(R.id.ib_detail_home).setOnClickListener {
            view.findNavController().navigate(R.id.action_detailFragment_to_listFragment)
        }

        if (type == 2) {
            (view.findViewById<ImageView>(R.id.iv_detail_stop)).visibility = View.INVISIBLE
            (view.findViewById<FloatingActionButton>(R.id.fab_detail_sell)).visibility = View.VISIBLE
        } else if (type == 1){
            (view.findViewById<ImageView>(R.id.iv_detail_stop)).visibility = View.VISIBLE
            (view.findViewById<FloatingActionButton>(R.id.fab_detail_sell)).visibility = View.INVISIBLE
        }

        view.findViewById<FloatingActionButton>(R.id.fab_detail_sell).setOnClickListener {
            val gson = Gson()
            val jsonString = gson.toJson(item)
            view.findNavController().navigate(
                R.id.action_detailFragment_to_sellingFragment, bundleOf("item" to jsonString))
        }

        view.findViewById<FloatingActionButton>(R.id.fab_detail_buy).setOnClickListener {
            val gson = Gson()
            val jsonString = gson.toJson(item)
            view.findNavController().navigate(
                R.id.action_detailFragment_to_buyingFragment, bundleOf("item" to jsonString))
        }

        (view.findViewById(R.id.tv_detail_sector) as TextView).text = item.sector
        (view.findViewById(R.id.tv_detail_security_type) as TextView).text = item.securityType
        (view.findViewById(R.id.tv_detail_volume) as TextView).text = item.volume.toString()
        (view.findViewById(R.id.tv_detail_title) as TextView).text = "${item.symbol.toUpperCase()}:"

        val sparkView = view.findViewById(R.id.sparkview_detail) as SparkView
        sparkView.lineColor = ContextCompat.getColor(context!!, R.color.color_bg)
        sparkView.lineWidth = 12f
        model = activity?.let { ViewModelProviders.of(it).get(StockWatchingViewModel::class.java) }!!
        model.allStocks.observe(
            this,
            Observer<List<StockWatchingItem>> { stocks ->
                stocks?.let {
                    stocks.forEach {
                        if (it.symbol.toLowerCase() == item.symbol.toLowerCase()) {
                            priceNow = it.lastSalePrice
                            (view.findViewById(R.id.tv_detail_ticker) as TickerView)
                                .setCharacterLists(priceNow.toString())
                            (view.findViewById(R.id.tv_detail_ticker) as TickerView)
                                .text = "$$priceNow"
                            val listType = object : TypeToken<ArrayList<Float>>() { }.type
                            points = gson.fromJson<ArrayList<Float>>(item.histories, listType)
                            sparkView.adapter = MyAdapter(points.toFloatArray())
                            sparkView.adapter.notifyDataSetChanged()
                        }

                    }
                }
            }
        )
        if (type == 2) {
            (view.findViewById<ImageView>(R.id.iv_detail_stop)).visibility = View.INVISIBLE
        } else if (type == 1){
            (view.findViewById<ImageView>(R.id.iv_detail_stop)).visibility = View.VISIBLE
        }
        (view.findViewById<ImageView>(R.id.iv_detail_stop)).setOnClickListener {
            (activity as SecondMainActivity).onChangeStockList(item, 1)
            Toast.makeText(activity, "Stopped watching ${item.symbol}", Toast.LENGTH_LONG).show()
        }
        viewAdapterNews = RecyclerViewAdapterNews(newsList, activity as SecondMainActivity)
        recyclerViewNews = view.findViewById(R.id.rv_news)
        recyclerViewNews.isNestedScrollingEnabled = false;
        recyclerViewNews.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerViewNews.adapter = viewAdapterNews
        getNews()
        return view
    }

    private fun getNews() {
        modelNews = activity?.let { ViewModelProviders.of(it).get(StockNewsViewModel::class.java) }!!
        modelNews.refreshNews(item.symbol)
        Log.d("news", item.symbol)
        modelNews.allNews!!.observe(
            this,
            Observer<NewsList> { news ->
                news?.let {
                    newsList.clear()
                    it.data.forEach {
                        newsList.add(it)
                    }
                    viewAdapterNews.notifyDataSetChanged()
                    recyclerViewNews.adapter = viewAdapterNews
                }
            })
    }


    class RecyclerViewAdapterNews(
        private var myDataSet: ArrayList<StockNewsItem>,
        private val activity: SecondMainActivity
    ) :
        RecyclerView.Adapter<RecyclerViewAdapterNews.ViewHolder>() {


        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RecyclerViewAdapterNews.ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.stock_news_item, parent, false)
            return ViewHolder(v, activity)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItems(myDataSet[position])
        }

        override fun getItemCount() = myDataSet.size

        class ViewHolder(private val view: View, private val activity: SecondMainActivity) : RecyclerView.ViewHolder(view) {
            @SuppressLint("SimpleDateFormat")
            fun bindItems(items: StockNewsItem) {
                val title: TextView = itemView.findViewById(R.id.tv_detail_news_title)
                title.text = items.title
                val date: TextView = itemView.findViewById(R.id.tv_detail_news_date)
                date.text = items.date
                Glide.with(this.activity)
                    .load(items.image_url)
                    .apply(RequestOptions().override(300, 300)).into(itemView.findViewById(R.id.ib_detail_news_image))
                itemView.setOnClickListener {
                    if (!items.news_url.isNullOrEmpty()) {
                        val openURL = Intent(Intent.ACTION_VIEW)
                        openURL.data = Uri.parse(items.news_url)
                        activity.startActivity(openURL)
                    }
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
