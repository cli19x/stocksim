package com.example.stock_searching
import com.example.stock_watching.StockWatchingItem
import com.example.stock_watching.StockWatchingRetrofitService
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface  StockSearchingRetrofitService {
    @GET("query?")
    fun getStockSearching(@Query("function") function: String,
                          @Query("apikey") api_key: String,
                          @Query("keywords") keywords: String): Observable<StockSearchList>


    companion object {
        fun create(baseUrl: String): StockSearchingRetrofitService {

            val retrofit = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()))
                .baseUrl(baseUrl)
                .build()

            return retrofit.create(StockSearchingRetrofitService::class.java)
        }
    }
}
