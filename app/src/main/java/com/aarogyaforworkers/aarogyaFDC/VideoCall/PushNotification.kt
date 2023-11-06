package com.aarogyaforworkers.aarogyaFDC

data class Data(
    val conferenceID:String,
    val token:String
)
data class PushNotification(
    val to: String,
    val data:Data
)