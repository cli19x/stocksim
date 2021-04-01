package com.example.stock_watching

import androidx.lifecycle.LiveData
import androidx.room.*
import retrofit2.http.GET

@Dao
interface StockWacthingItemDao{

    @Query("SELECT * FROM stock_table_six order BY symbol ASC")
    fun getWatchingStockList(): LiveData<List<StockWatchingItem>>

    @Query ("DELETE FROM stock_table_six")
    fun deleteAll()

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertWatchingStock(userItem: StockWatchingItem)

    @Query("SELECT * FROM stock_table_six WHERE symbol = :symbol")
    fun getAStock(symbol: String): LiveData<StockWatchingItem>

    @Delete
    fun deleteOne(stockWatchingItem: StockWatchingItem)

}