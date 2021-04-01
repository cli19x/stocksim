package com.example.user

import android.service.autofill.FillEventHistory
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.util.*

data class UserStockItem(

    @PrimaryKey @ColumnInfo(name = "symbol") var symbol: String,
    @ColumnInfo(name = "shares") var shares: Int,
    @ColumnInfo(name = "price") var price: Float,
    @ColumnInfo(name = "histories") var history: String
)


