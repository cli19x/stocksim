package com.example.stockgame

import android.R.attr.*
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.stock_watching.StockWatchingItem
import com.example.stock_watching.StockWatchingViewModel
import com.example.user.UserItem
import com.example.user.UserViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.robinhood.ticker.TickerView
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.core.os.bundleOf


class BuyingFragment : Fragment() {



    private val COST = "cost_amount"
    private val SHARES = "shares"
    private val SYMBOL = "symbol"
    private lateinit var modelOne: UserViewModel
    private lateinit var modelTwo: StockWatchingViewModel
    private var cost = 0f
    private var confirmShares = 0
    private var totalCost = 0f
    private var totalPrice = 0f
    private var investing = 0f
    private lateinit var item: StockWatchingItem
    private lateinit var liveItem: StockWatchingItem
    private lateinit var txtCost:TextView
    private lateinit var etShares:EditText
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_buying, container, false)
        etShares = (view.findViewById(R.id.et_buy_shares))
        txtCost = (view.findViewById(R.id.tv_buy_cost) as TextView)
        Log.d("saved2","================== ${txtCost.text}")
        if (savedInstanceState != null) {
            etShares.setText(savedInstanceState.getString(SHARES))
            Log.d("saved3","================== ${(view.findViewById<EditText>(R.id.et_buy_shares)).text}")
            txtCost.text = savedInstanceState.getString(COST)
            Log.d("saved4","================== ${txtCost.text}")
            (view.findViewById(R.id.tv_buying_price) as TextView).text = savedInstanceState.getString(SYMBOL)
        }

        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    val gson = Gson()
                    val jsonString = gson.toJson(item)
                    view.findNavController().navigate(R.id.action_buyingFragment_to_detailFragment,
                        bundleOf("stock_json" to jsonString, "type" to 1))
                }
                return false
            }
        })
        val gson = Gson()
        item = gson.fromJson(this.arguments?.getString("item"), StockWatchingItem::class.java)
        modelOne = activity?.let { ViewModelProviders.of(it).get(UserViewModel::class.java) }!!
        modelOne.user.observe(
            this,
            Observer<UserItem> { user ->
                user?.let {
                    investing = it.investing
                    Log.d("buy", "${it.investing}-----------------------------")
                }
            }
        )
        Log.d("buy_frg", "---------------------- $item          ${item.symbol}")
        (view.findViewById(R.id.tv_buying_price) as TextView).text = item.symbol
        modelTwo = activity?.let { ViewModelProviders.of(it).get(StockWatchingViewModel::class.java) }!!
        modelTwo.allStocks.observe(
            this,
            Observer<List<StockWatchingItem>> { stocks ->
                stocks?.let {
                    var temp = 0.0f
                    totalPrice = 0f
                    it.forEach {
                        if (it.isOwning) {
                            temp += (it.lastSalePrice - it.ownPrice) * it.shares
                            totalPrice += it.ownPrice * it.shares
                        }
                        if (it.symbol.toLowerCase() == item.symbol.toLowerCase()) {
                            liveItem = it
                            (view.findViewById<TextView>(R.id.tv_buy_price)).text = "$${it.lastSalePrice}"
                        }
                    }
                    if (txtCost.text == "") {
                        txtCost.hint =
                            "Buying Power ${investing - totalPrice}"
                    }
                    cost = liveItem.lastSalePrice
                }
            }
        )
        etShares.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!etShares.text.isNullOrEmpty()) {
                    etShares.text.toString().toIntOrNull()!!.let {
                        if (it > 0) {
                            txtCost.text = (cost * it).toString()
                        }
                    }
                }
            }
            override fun afterTextChanged(s: Editable) {
                if (!etShares.text.isNullOrEmpty()) {
                    etShares.text.toString().toIntOrNull()!!.let {
                        if (it > 0) {
                            txtCost.text = (cost * it).toString()
                            totalCost = cost * it
                            confirmShares = it
                        }
                    }
                }
            }
        })

        (view.findViewById<FloatingActionButton>(R.id.fab_buy_confirm)).setOnClickListener {
            if (totalCost > investing - totalPrice) {
                Toast.makeText(activity, "Cost cannot over buying power!!!", Toast.LENGTH_LONG).show()
            }
            else {
                item.lastSalePrice = liveItem.lastSalePrice
                item.shares = confirmShares
                item.ownPrice = liveItem.lastSalePrice
                item.isOwning = true
                (activity as SecondMainActivity).onChangeStockList(item, 0)
                view.findNavController().navigate(R.id.action_buyingFragment_to_listFragment)
            }

        }
        (view.findViewById<ImageButton>(R.id.ib_buy_home)).setOnClickListener {
            val gson = Gson()
            val jsonString = gson.toJson(item)
            view.findNavController().navigate(R.id.action_buyingFragment_to_detailFragment,
                bundleOf("stock_json" to jsonString, "type" to 1))
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("saved","================== ${totalCost}")
        outState.putString(COST, txtCost.text.toString())
        outState.putString(SHARES, etShares.text.toString())
        outState.putString(SYMBOL, item.symbol)
        super.onSaveInstanceState(outState)
    }
}
