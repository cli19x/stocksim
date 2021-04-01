package com.example.stock_news

import com.google.gson.GsonBuilder
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface  StockNewsRetrofitService {
    @GET("v1?")
    fun getStockNews(@Query("tickers") symbol: String,
                     @Query("items") items: String,
                     @Query("token") api_key: String
                        ): Observable<NewsList>

    companion object {
        fun create(baseUrl: String): StockNewsRetrofitService {
            val retrofit = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()))
                .baseUrl(baseUrl)
                .build()

            return retrofit.create(StockNewsRetrofitService::class.java)
        }
    }
}
