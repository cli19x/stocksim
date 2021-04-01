package com.example.stock_watching
import android.util.Log
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface  StockWatchingRetrofitService {
    @GET("stable/tops")
    fun getStockWatching(@Query("token") api_key: String,
                         @Query("symbols") symbol: String): Observable<ArrayList<StockWatchingItem>>

    companion object {
        fun create(baseUrl: String): StockWatchingRetrofitService {
            val retrofit = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()))
                .baseUrl(baseUrl)
                .build()

            return retrofit.create(StockWatchingRetrofitService::class.java)
        }
    }
}
