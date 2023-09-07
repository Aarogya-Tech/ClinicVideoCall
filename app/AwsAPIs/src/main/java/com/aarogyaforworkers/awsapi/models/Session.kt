package com.aarogyaforworkers.awsapi.models

//data class Session(val date : String, val time : String, var sessionId : String, val deviceId : String, var userId : String, val adminId : String, val sys : String, val dia : String, val heartRate : String, val spO2 : String,
//                   val weight : String, val bodyFat : String, val temp : String, var ecgFileLink : String, val questionerAnswers : String, var remarks : String, val location : String)

data class Session(
    var date : String,
    var time : String,
    var deviceId: String,
    var userId: String,
    var adminId: String,
    var sessionId: String,
    var sys: String,
    var dia: String,
    var heartRate: String,
    var spO2: String,
    var weight: String,
    var bodyFat: String,
    var temp: String,
    var ecgFileLink: String,
    var PhysicalExamination: String,
    var LabotryRadiology: String,
    var ImpressionPlan: String,
    var questionerAnswers: String,
    var remarks: String,
    var location: String
)

