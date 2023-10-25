package com.aarogyaforworkers.awsapi.models

//data class SubUserProfile(val user_id : String, var phone : String, var isUserVerified : String, var frist_name : String, var last_name : String, var dob : String, var gender : String, var height : String, val location : String, var profile_pic_url : String, var medical_history : String, val total_sessions_done : String, var SecAns : String)

data class SubUserProfile(
    var user_id: String,
    var admin_id: String,
    var caretaker_id: String,
    var phone: String,
    var isUserVerified: Boolean,
    var first_name: String,
    var last_name: String,
    var dob: String,
    var gender: String,
    var height: String,
    var location: String,
    var reminder: String,
    var profile_pic_url: String,
    var medical_history: String,
    var SecAns: String,
    var chiefComplaint: String,
    var HPI_presentIllness: String,
    var FamilyHistory: String,
    var SocialHistory: String,
    var PastMedicalSurgicalHistory: String,
    var Medication: String,
    var country_code : String
)
