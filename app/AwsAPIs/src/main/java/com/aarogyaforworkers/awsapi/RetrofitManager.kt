package com.aarogyaforworkers.awsapi

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class RetrofitManager {

    private val baseURL = "https://6ckw6qu8t4.execute-api.ap-south-1.amazonaws.com/v1/"

    val client = OkHttpClient()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T: Any> myApi(classType: Class<T>): T {
        return retrofit.create(classType)
    }
}

