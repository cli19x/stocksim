package com.example.stock_searching

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stock_watching.StockWatchingItem
import com.example.stock_watching.StockWatchingItemRepository
import com.example.stock_watching.StockWatchingRoomDatabase
import com.example.stockgame.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class StockSearchViewModel(application: Application): AndroidViewModel(application){

    private var disposable: Disposable? = null

    val searchingStocks: MutableLiveData<StockSearchList> = MutableLiveData()


    fun refreshStocks(str: String){
        disposable =
            StockSearchingRetrofitService.create(MainActivity.URL_TWO)
                .getStockSearching("SYMBOL_SEARCH", MainActivity.APIKEY_TWO, str)
                .subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    {result -> showResult(result)},
                    {error -> showError(error)})
    }


    private fun showError(error: Throwable?) {
        Log.d("t04","Error:"+error?.toString())
    }

    private fun showResult(result: StockSearchList?) {
        Log.d("hahat", result.toString())
        searchingStocks.value =  null
        searchingStocks.value = result
    }


}