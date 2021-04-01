package com.example.user

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class UserItemRepository(private val userDao: UserItemDao){

    val user: LiveData<UserItem> = userDao.getUser()

    @WorkerThread
    fun insert(userItem: UserItem){
        userDao.insertUser(userItem)
    }

    @WorkerThread
    fun deleteAll(){
        userDao.deleteAll()
    }


}