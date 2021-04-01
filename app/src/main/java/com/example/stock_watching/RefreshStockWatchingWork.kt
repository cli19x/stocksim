package com.example.stock_watching

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.stockgame.MainActivity
import com.example.user.UserStockItem
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class RefreshStockWatchingWork(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private val MAX_SIZE = 20
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: StockWatchingItemRepository =
        StockWatchingItemRepository(StockWatchingRoomDatabase.getDatabase(appContext).stockWatchingDao())

    private var disposable: Disposable? = null

    private var isSuccess: Boolean = false

    override fun doWork(): Result {

        val itemsStr:String = inputData.getString("items")!!
        val gson = GsonBuilder().setPrettyPrinting().create()
        val stockList: ArrayList<StockWatchingItem> = gson.fromJson(itemsStr,
            object : TypeToken<ArrayList<StockWatchingItem>>() {}.type)
        var symbols = ""
        stockList.forEach {
            symbols += it.symbol+","
        }
        symbols = symbols.substring(0 , symbols.length - 1)
        Log.d("work_stock_watching", "------------------- $symbols --------------------------")
        refreshStocks(symbols, stockList)
        return if (isSuccess){
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun refreshStocks(symbol: String, item: ArrayList<StockWatchingItem>){
        disposable =
            StockWatchingRetrofitService.create(MainActivity.URL)
                .getStockWatching(MainActivity.APIKEY, symbol)
                .subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    {result -> showResult(result, item)},
                    {error -> showError(error)})

    }


    private fun showError(error: Throwable?) {
        isSuccess = false
        Log.d("t04","Error:"+error?.toString())
    }


    private fun showResult(result: ArrayList<StockWatchingItem>, item: ArrayList<StockWatchingItem>) {
        result.sortBy {
            it.symbol
        }

        item.sortBy {
            it.symbol
        }
        val gson = Gson()
        var index = 0
        result.forEach{
            Log.d("haha_work", "${it.symbol} ============================")
            Log.d("Stock_haha1", it.toString())
            it.shares = item[index].shares
            Log.d("Stock_haha2",  result[index].lastSalePrice.toString())
            it.ownPrice = item[index].ownPrice
            Log.d("Stock_haha3",  result[index].lastSalePrice.toString())
            val listType = object : TypeToken<ArrayList<Float>>() { }.type
            Log.d("Stock_haha4",  result[index].lastSalePrice.toString())
            val newList = gson.fromJson<ArrayList<Float>>(item[index].histories, listType)
            Log.d("Stock_haha5",  newList.toString())
            newList.add(it.lastSalePrice)
            Log.d("Stock_haha6",  result[index].lastSalePrice.toString())
            if (newList.size > MAX_SIZE) {
                newList.removeAt(0)
            }
            Log.d("Stock_haha7",  result[index].lastSalePrice.toString())
            val jsonString = gson.toJson(newList)
            Log.d("Stock_haha8",  result[index].lastSalePrice.toString())
            it.histories = jsonString
            Log.d("Stock_haha9",  result[index].lastSalePrice.toString())
            it.fullName = item[index].fullName
            Log.d("Stock_haha10",  result[index].lastSalePrice.toString())
            it.isOwning = item[index].shares > 0
            Log.d("Stock_haha11",  result[index].lastSalePrice.toString())
            insertWatching(result[index])
            Log.d("Stock_haha12",  result[index].lastSalePrice.toString())
            Log.d("work12", it.toString())
            index++
        }
        isSuccess = true
    }

    private fun insertWatching(stock: StockWatchingItem) = scope.launch(Dispatchers.IO) {
        repository.insertWatching(stock)
    }
}