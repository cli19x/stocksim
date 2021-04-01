package com.example.stock_watching

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "stock_table_six")
data class StockWatchingItem(@PrimaryKey @ColumnInfo(name = "symbol") var symbol: String,
                             @ColumnInfo(name = "fullName") var fullName: String?,
                             @ColumnInfo(name = "sector") var sector: String,
                             @ColumnInfo(name = "securityType") var securityType: String,
                             @ColumnInfo(name = "bidPrice") var bidPrice: Float,
                             @ColumnInfo(name = "bidSize") var bidSize: Int,
                             @ColumnInfo(name = "askPrice") var askPrice: Float,
                             @ColumnInfo(name = "askSize") var askSize: Int,
                             @ColumnInfo(name = "lastUpdated") var lastUpdated: Long,
                             @ColumnInfo(name = "lastSalePrice") var lastSalePrice: Float,
                             @ColumnInfo(name = "lastSaleSize") var lastSaleSize: Int,
                             @ColumnInfo(name = "lastSaleTime") var lastSaleTime: Long,
                             @ColumnInfo(name = "volume") var volume: Long,
                             @ColumnInfo(name = "shares") var shares: Int,
                             @ColumnInfo(name = "ownPrice")  var ownPrice: Float,
                             @ColumnInfo(name = "histories") var histories: String?,
                             @ColumnInfo(name = "isOwing") var isOwning: Boolean


) {
    constructor():this(" ", " ", " ", " ", 0f, 0, 0f, 0, 0
        , 0f, 0, 0, 0, 0, 0f, " ", false)
}

/**
 *
 *
[{"symbol":"AAPL",
"sector":"electronictechnology",
"securityType":"cs",
"bidPrice":0,
"bidSize":0,
"askPrice":0,
"askSize":0,
"lastUpdated":1555448241549,
"lastSalePrice":199.35,
"lastSaleSize":100,
"lastSaleTime":1555444797044,
"volume":809521}]
},
 */


