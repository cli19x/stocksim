package com.example.stockgame
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.stock_watching.StockWatchingItem
import com.example.stock_watching.StockWatchingViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.robinhood.ticker.TickerView
import java.util.*

class SellingFragment: Fragment() {

    private lateinit var model: StockWatchingViewModel
    private var priceNow = 0f
    private lateinit var liveItem: StockWatchingItem
    private lateinit var item: StockWatchingItem
    private lateinit var etShares: EditText

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_selling, container, false)
        val gson = Gson()
        view.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    val jsonString = gson.toJson(item)
                    view.findNavController().navigate(R.id.action_sellingFragment_to_detailFragment,
                        bundleOf("stock_json" to jsonString, "type" to 2))
                }
                return false
            }
        })
        item = gson.fromJson(this.arguments?.getString("item"), StockWatchingItem::class.java)

        (view.findViewById<TextView>(R.id.tv_sell_shares)).text = item.symbol
        etShares = view.findViewById(R.id.et_sell_shares)

        etShares.hint = "${item.shares} shares"

        etShares.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                if (!etShares.text.isNullOrEmpty()) {
                    etShares.text.toString().toIntOrNull()!!.let {
                        if (it > 0 && it <= item.shares) {
                            (view.findViewById<TextView>(R.id.tv_sell_income)).text =
                                "$${"%.2f".format((etShares.text).toString().toInt() * priceNow)}"
                        } else {
                            (view.findViewById<TextView>(R.id.tv_sell_income)).text = "0"

                        }
                    }
                }
            }
        })

        (view.findViewById<ImageButton>(R.id.ib_sell_home)).setOnClickListener {
            val gson = Gson()
            val jsonString = gson.toJson(item)
            view.findNavController().navigate(R.id.action_sellingFragment_to_detailFragment,
                bundleOf("stock_json" to jsonString, "type" to 2))
        }



        (view.findViewById<FloatingActionButton>(R.id.fab_sell_confirm)).setOnClickListener {
            if ((etShares.text).toString().toInt() > item.shares || (etShares.text).toString().toInt() < 0) {
                Toast.makeText(activity, "Cannot selling more shares that you own!!!", Toast.LENGTH_LONG).show()
            }
            else {
                item.shares -= (etShares.text).toString().toInt()
                item.lastSalePrice = liveItem.lastSalePrice
                if (item.shares == 0) {
                    item.isOwning = false
                    item.ownPrice = 0f
                }
                (activity as SecondMainActivity).onChangeStockList(item, 0)
                view.findNavController().navigate(R.id.action_sellingFragment_to_listFragment)
            }
        }

        model = activity?.let { ViewModelProviders.of(it).get(StockWatchingViewModel::class.java) }!!
        model.allStocks.observe(
            viewLifecycleOwner,
            Observer<List<StockWatchingItem>> { stocks ->
                stocks?.let {
                    it.forEach {
                        if (it.symbol.equals(item.symbol, ignoreCase = true)) {
                            liveItem = it
                            priceNow = it.lastSalePrice
                            (view.findViewById<TextView>(R.id.tv_sell_price)).text = "$$priceNow"
                        }
                    }
                }
            }
        )
        return view
    }
}
