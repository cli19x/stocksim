package com.example.stockgame

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.*
import com.example.stock_watching.RefreshStockWatchingWork
import com.example.stock_watching.StockWatchingItem
import com.example.stock_watching.StockWatchingViewModel
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit

class SecondMainActivity : AppCompatActivity() {


    val FLAG = "flag"
    private lateinit var modelTwo: StockWatchingViewModel
    private var flag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_second_main)
        if (savedInstanceState != null) {
            flag = savedInstanceState.getInt(FLAG)
        }

        modelTwo = this.let { ViewModelProviders.of(it).get(StockWatchingViewModel::class.java) }
        modelTwo.allStocks.observe(
            this,
            Observer<List<StockWatchingItem>> { stocks ->
                stocks?.let {
                    periodicWorkOne(it)
                    Log.d("hahahaMain","+++++++++++++++++++++++++++++++")
                }
            }
        )

    }


    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }


    private fun periodicWorkOne(items: List<StockWatchingItem>) {
        if (items.isNullOrEmpty()) {
            modelTwo.pullFromFireBase()
        }
        val gson = GsonBuilder().setPrettyPrinting().create()
        val itemsStr: String = gson.toJson(items)

        WorkManager.getInstance().beginUniqueWork(
            Companion.TAG, ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<RefreshStockWatchingWork>()
                .setInitialDelay(1, TimeUnit.SECONDS).setInputData(
                    workDataOf("items" to itemsStr)
                ).build()).enqueue()

    }

    fun onChangeStockList(item: StockWatchingItem, type: Int) {
        WorkManager.getInstance().cancelAllWork()
        if (type == 0) {
            modelTwo.insertNewStock(item)
            flag = 0
        }
        else if (type == 1) {
            modelTwo.delAStock(item)
            flag = 0
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(FLAG, flag)
        super.onSaveInstanceState(outState)
    }

    public override fun onDestroy() {
        super.onDestroy()
        WorkManager.getInstance().cancelAllWork()
    }

    companion object {
        const val TAG = "PP05_TAG"
    }
}
