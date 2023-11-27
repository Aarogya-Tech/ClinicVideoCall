package com.aarogyaforworkers.aarogyaFDC.VideoCall

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.aarogyaforworkers.aarogyaFDC.Constants.Companion.BASE_URL
import com.google.errorprone.annotations.Keep

@Keep
class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI::class.java)
        }
    }
}