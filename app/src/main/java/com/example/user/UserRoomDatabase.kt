package com.example.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [UserItem::class], version = 1, exportSchema = false)
abstract class UserRoomDatabase: RoomDatabase(){
    abstract fun userDao(): UserItemDao
    companion object {
        @Volatile
        private var INSTANCE: UserRoomDatabase? = null
        fun getDatabase(
            context: Context
        ): UserRoomDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserRoomDatabase::class.java,
                    "User_database_seven"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}