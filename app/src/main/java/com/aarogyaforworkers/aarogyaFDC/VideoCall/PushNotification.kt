package com.aarogyaforworkers.aarogyaFDC

data class data(
    val conferenceID:String
)
data class PushNotification(
    val to: String,
    val data:Data
)