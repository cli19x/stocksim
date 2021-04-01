package com.example.stock_graph

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "stock_graph")
data class StockGraphItem(@PrimaryKey @ColumnInfo(name = "time") var time: String,
                          @ColumnInfo(name = "data") var data: Float


)




