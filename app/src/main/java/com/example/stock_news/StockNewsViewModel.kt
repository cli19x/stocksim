package com.example.stock_news

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stockgame.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class StockNewsViewModel(application: Application): AndroidViewModel(application){

    private var disposable: Disposable? = null
    val allNews: MutableLiveData<NewsList>? = MutableLiveData()

    fun refreshNews(symbol: String){
        disposable =
            StockNewsRetrofitService.create(MainActivity.URL_THREE)
                .getStockNews(symbol, "10", MainActivity.APIKEY_THREE)
                .subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()).subscribe(
                    {result -> showResult(result)},
                    {error -> showError(error)})
    }


    private fun showError(error: Throwable?) {

        Log.d("t04_haha","Error:"+error?.toString())
    }

    private fun showResult(result: NewsList) {
        allNews!!.value = result
    }

}