package com.aarogyaforworkers.aarogyaFDC

data class Data(
    val conferenceID:String
)
data class PushNotification(
    val to: String,
    val data:Data
)