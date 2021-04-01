package com.example.stock_graph

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class StockGraphViewModel(application: Application): AndroidViewModel(application){

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    private val repository: StockGraphItemRepository
    val allStocks: LiveData<List<StockGraphItem>>

    val list: ArrayList<StockGraphItem> = ArrayList()
    init {
        val stockDao = StockGraphRoomDatabase.getDatabase(application).stockGraphDao()
        repository = StockGraphItemRepository(stockDao)
        allStocks = repository.allWatchingStocks
        if (allStocks.value.isNullOrEmpty()) {

        }
    }

    fun insertNewPoint(stock: StockGraphItem) {
        insertAPoint(stock)
    }


    fun delAPoint(stock: StockGraphItem) {
        deleteOneStock(stock)
    }

    private fun insertAPoint(point: StockGraphItem) = scope.launch(Dispatchers.IO) {
        repository.insertGraphPoint(point)
    }

    private fun deleteAll() = scope.launch (Dispatchers.IO){
        repository.deleteAll()
    }

    private fun deleteOneStock(stock: StockGraphItem) = scope.launch (Dispatchers.IO) {
        repository.deleteOne(stock)
    }
}