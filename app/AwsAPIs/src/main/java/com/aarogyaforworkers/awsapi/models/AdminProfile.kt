package com.aarogyaforworkers.awsapi.models

data class AdminProfile(val admin_id : String, val email : String, val phone : String, var first_name : String, val last_name : String, val age : String, val gender : String, val weight : String, val height : String, var location : String, var profile_pic_url : String, val total_sessions_taken : String,
                        val total_users_added : String, val isVerified : String, val hospitalName : String, var designation : String, val isDoctor : String, val groups : String, val groupid : String, val registration_id : String)