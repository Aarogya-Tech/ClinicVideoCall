package com.aarogyaforworkers.aarogyaFDC

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Data(
    @SerializedName("conferenceID")
    val conferenceID:String,
    @SerializedName("token")
    val token:String
)
@Keep
data class PushNotification(
    @SerializedName("to")
    val to: String,
    @SerializedName("data")
    val data:Data
)