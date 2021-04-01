package com.example.stock_watching

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [StockWatchingItem::class], version = 1, exportSchema = false)
abstract class StockWatchingRoomDatabase: RoomDatabase(){
    abstract fun stockWatchingDao(): StockWacthingItemDao
    companion object {
        @Volatile
        private var INSTANCE: StockWatchingRoomDatabase? = null
        fun getDatabase(
            context: Context
        ): StockWatchingRoomDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockWatchingRoomDatabase::class.java,
                    "Stock_database_nine"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    
}