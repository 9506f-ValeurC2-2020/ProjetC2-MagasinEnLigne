package com.cnam.magasinenligne.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AppRetrofitClient {

    fun buildService(i: Int): RetrofitInterface {
        val baseUrl = when (i) {
            1 -> BASE_URL_1
            2 -> BASE_URL_2
            3 -> BASE_URL_3
            4 -> BASE_URL_4
            else -> BASE_URL_5
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(requestHeader)
            .build()
            .create(RetrofitInterface::class.java)
    }


    private val requestHeader: OkHttpClient
        get() = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
}