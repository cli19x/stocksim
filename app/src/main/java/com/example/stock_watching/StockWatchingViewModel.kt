package com.example.stock_watching

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class StockWatchingViewModel(application: Application): AndroidViewModel(application){

    private var parentJob = Job()
    val allInitStocks: MutableList<StockWatchingItem> = mutableListOf()
    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main

    private val scope = CoroutineScope(coroutineContext)

    private val repository: StockWatchingItemRepository
    val allStocks: LiveData<List<StockWatchingItem>>

    private var aStock: LiveData<StockWatchingItem>? = null
    val list: ArrayList<StockWatchingItem> = ArrayList()
    init {
        val stockDao = StockWatchingRoomDatabase.getDatabase(application).stockWatchingDao()
        repository = StockWatchingItemRepository(stockDao)
        allStocks = repository.allWatchingStocks
        if (allStocks.value.isNullOrEmpty()) {

        }
    }

    fun insertNewStock(stock: StockWatchingItem) {
        insertWatching(stock)
    }

    fun getAStock(symbol: String): StockWatchingItem {
        getOneStock(symbol)
        return aStock!!.value!!
    }

    fun delAStock(stock: StockWatchingItem) {
        deleteOneStock(stock)
    }

    private fun insertWatching(stock: StockWatchingItem) = scope.launch(Dispatchers.IO) {
        repository.insertWatching(stock)
    }

    private fun deleteAll() = scope.launch (Dispatchers.IO){
        repository.deleteAll()
    }

    private fun deleteOneStock(stock: StockWatchingItem) = scope.launch (Dispatchers.IO) {
        repository.deleteOne(stock)
    }

    private fun getOneStock(stock: String) = scope.launch (Dispatchers.IO) {
        Log.d("model", "-----------------$stock")
        aStock = repository.getStock(stock)
    }

    private fun initialList() {
        allInitStocks.forEach {
            Log.d("init_list", "${it.symbol}")
            val listOfHistory: ArrayList<Float> = ArrayList()
            val gson = Gson()
            val jsonString = gson.toJson(listOfHistory)
            val temp = StockWatchingItem(it.symbol!!, it.fullName, " "," ",
                0f, 0, 0f, 0, 0,0f,
                0, 0, 0, 0, 0f, jsonString, false)
            insertWatching(temp)
        }
    }
    fun pullFromFireBase() {
        val listOfHistory: ArrayList<Float> = ArrayList()
        val gson = Gson()
        val jsonString = gson.toJson(listOfHistory)
        val stockItem1 = StockWatchingItem("IQ", " IQIYI Inc", " "," ",
            0f, 0, 0f, 0, 0,0f,
            0, 0, 0, 0, 0f, jsonString, false)
        database.child("stock").child(stockItem1.symbol!!).setValue(stockItem1)
        val locationListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                allInitStocks.clear()
                dataSnapshot.children.mapNotNullTo(allInitStocks) { it.getValue<StockWatchingItem>(StockWatchingItem::class.java) }
                initialList()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }
        }
        database.child("stock").addListenerForSingleValueEvent(locationListener)
    }
}