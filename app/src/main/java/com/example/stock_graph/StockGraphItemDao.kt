package com.example.stock_graph

import androidx.lifecycle.LiveData
import androidx.room.*
import retrofit2.http.GET

@Dao
interface StockGraphItemDao{

    @Query("SELECT * FROM stock_graph")
    fun getGraphPointList(): LiveData<List<StockGraphItem>>

    @Query ("DELETE FROM stock_graph")
    fun deleteAll()

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertGraphPoint(userItem: StockGraphItem)

    @Query("SELECT * FROM stock_graph WHERE time = :time")
    fun getAPoint(time: String): LiveData<StockGraphItem>

    @Delete
    fun deleteAPoint(stockWatchingItem: StockGraphItem)

}