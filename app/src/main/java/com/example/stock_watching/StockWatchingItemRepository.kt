package com.example.stock_watching

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class StockWatchingItemRepository(private val stockWatchingDao: StockWacthingItemDao){

    val allWatchingStocks: LiveData<List<StockWatchingItem>> = stockWatchingDao.getWatchingStockList()
    @WorkerThread
    fun insertWatching(stockWatchingItem: StockWatchingItem){
        stockWatchingDao.insertWatchingStock(stockWatchingItem)
    }

    @WorkerThread
    fun deleteAll(){
        stockWatchingDao.deleteAll()
    }

    @WorkerThread
    fun getStock(symbol: String): LiveData<StockWatchingItem> {
        return stockWatchingDao.getAStock(symbol)
    }

    @WorkerThread
    fun deleteOne(symbol: StockWatchingItem) {
        stockWatchingDao.deleteOne(symbol)
    }


}