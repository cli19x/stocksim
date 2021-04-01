package com.example.user

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UserViewModel(application: Application): AndroidViewModel(application){

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob+ Dispatchers.Main


    private val scope = CoroutineScope(coroutineContext)



    private val repository: UserItemRepository =
        UserItemRepository(UserRoomDatabase.getDatabase(application).userDao())

    val user: LiveData<UserItem>

    init {
        user = repository.user
    }

    fun changingFund(fund: Float) {
        val newUser = UserItem(0, fund)
        insert(newUser)
    }


    private fun insert(user: UserItem) = scope.launch(Dispatchers.IO) {
        repository.insert(user)
    }

    private fun deleteAll() = scope.launch (Dispatchers.IO){
        repository.deleteAll()
    }


}