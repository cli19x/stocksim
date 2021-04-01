package com.example.user

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Delete



@Dao
interface UserItemDao{

    @Query("SELECT * FROM user_table_two")
    fun getUser(): LiveData<UserItem>

    @Query ("DELETE FROM user_table_two")
    fun deleteAll()

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(userItem: UserItem)
}