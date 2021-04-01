package com.example.stock_graph

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [StockGraphItem::class], version = 1, exportSchema = false)
abstract class StockGraphRoomDatabase: RoomDatabase(){
    abstract fun stockGraphDao(): StockGraphItemDao
    companion object {
        @Volatile
        private var INSTANCE: StockGraphRoomDatabase? = null
        fun getDatabase(
            context: Context
        ): StockGraphRoomDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StockGraphRoomDatabase::class.java,
                    "Graph_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
    
}