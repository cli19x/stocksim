package com.example.stock_graph

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class StockGraphItemRepository(private val stockGraphItemDao: StockGraphItemDao){

    val allWatchingStocks: LiveData<List<StockGraphItem>> = stockGraphItemDao.getGraphPointList()
    @WorkerThread
    fun insertGraphPoint(stockWatchingItem: StockGraphItem){
        stockGraphItemDao.insertGraphPoint(stockWatchingItem)
    }

    @WorkerThread
    fun deleteAll(){
        stockGraphItemDao.deleteAll()
    }

    @WorkerThread
    fun getStock(time: String): LiveData<StockGraphItem> {
        return stockGraphItemDao.getAPoint(time)
    }

    @WorkerThread
    fun deleteOne(point: StockGraphItem) {
        stockGraphItemDao.deleteAPoint(point)
    }


}