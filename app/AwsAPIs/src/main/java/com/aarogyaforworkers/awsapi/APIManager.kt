package com.aarogyaforworkers.awsapi

import android.util.Log
import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.aarogyaforworkers.awsapi.models.Registration_Count
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class APIManager {

    private val retrofitManager = RetrofitManager()

    private val adminApi = retrofitManager.myApi(AdminAPIs::class.java)

    private var loggedInUser = AdminProfile("","","","","","","","","","","","","")

    var callback : APICallbacks? = null

    fun initializeApiManager(callbacks: APICallbacks){
        callback = callbacks
    }

    fun getProfile(query : String, isAdmin : Boolean){
        when(isAdmin){
            true -> executeAdminAPICall(query)
            false ->executeAdminSearchAPICall(query)
        }
    }

    fun getSubUserProfile(userId : String){
        executeGetSubUserProfile(adminApi.getSubUsersProfileById(userId))
    }

    fun getLoggedInAdminProfile() = loggedInUser

    fun resetLoggedInUser() {
        loggedInUser = AdminProfile("","","","","","","","","","","","","")
    }

    fun getSubUserByPhone(phone : String){
        if(phone.isEmpty()){
            callback?.userIsNotRegistered()
        }else{
            executeGetUserByPhone(adminApi.getSubUsersProfileByPhone(phone))
        }
    }

    fun deleteGuestAllSession(adminId : String){
        executeDeleteUserSession(adminApi.deleteSessionForUser(adminId))
    }

    fun getAdminProfileByPhone(phone: String, password : String){
//        executeAdminAPICallByPhone("1$phone", password)
        executeAdminAPICallByPhone(phone, password)
    }

    fun createNewSubUser(user : SubUserProfile){
        executeCreateUpdateSubUserAPICall(adminApi.createNewSubUser(user))
    }

    fun updateSubUser(user : SubUserProfile){
        executeCreateUpdateSubUserAPICall(adminApi.updateSubUser(user))
    }

    fun sendVerificationOTP(phone: String){

        // For now set it only for US with +1
//        executeAndParseVerificationOTP(adminApi.sendSubUserVerificationCode("+1"+phone))

        executeAndParseVerificationOTP(adminApi.sendSubUserVerificationCode(phone))
    }

    // Registration Counts -
    fun getRegistrationCount(byId : String = "AAClinicNP"){
        val call : Call<ResponseBody> = adminApi.getRegistrationCounts(byId)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    val responseString = response.body()!!.string()
                    val responseJson = Gson().fromJson(responseString, JsonObject::class.java)
                    val recordsArray = responseJson.get("records").asJsonArray
                    if(!recordsArray.isEmpty){
                        for (record in recordsArray) {
                            val recordArray = record.asJsonArray
                            val registrationId = recordArray[0].asJsonObject.get("stringValue").asString
                            val registrationCount = recordArray[1].asJsonObject.get("longValue").asInt
                            //ATNP0001->
                            callback?.onSuccessGetTotalRegistrationCounts(registrationCount)
                        }
                    }else{
                        callback?.onSuccessGetTotalRegistrationCounts(0)
                    }
                }else{
                    callback?.onFailedToGetRegistrationCount()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedToGetRegistrationCount()
            }
        })
    }

    fun updateRegistrationCount(registrationCount: Registration_Count){
        val call : Call<ResponseBody> = adminApi.updateRegistrationCounts(registrationCount)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    callback?.onSuccessRegistrationCountUpdated()
                }else{
                    callback?.onFailedToUpdateRegistrationCount()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedToUpdateRegistrationCount()
            }
        })

    }

    fun getSessionByUserId(userId: String){
        if(userId.isNotEmpty()) executeGetSessionsByUserID(adminApi.getSessionByUserID(userId))
    }

    fun updateAdminProfilePic(profile: AdminProfile){
        executeUpdateAdminProfilePic(profile)
    }

    fun deleteSessionById(sessionId : String){
        executeDeleteSessionBYSessionID(sessionId)
    }

    fun updateSessionBySessionId(session: Session){
        executeUpdateSessionRemarkBySessionId(adminApi.updateSessionBySessionId(session))
    }

    fun updateFullSession(session: Session){
        executeUpdateFullSessionBySessionId(adminApi.updateFullSessionBySessionId(session))
    }

    fun createSession(session: Session){
//        executeCreateSession(adminApi.createNewSession(session))
    }

    fun createPatientSession(session: Session){
        executeCreateSession(adminApi.createNewPatientSession(session))
    }

    fun updatePatientSession(session: Session){
        executeUpdateSessionSessionId(adminApi.updateSessionBySessionId(session))
    }

    fun deletePatientSession(sessionId: String){
        val call = adminApi.deletePatientSession(sessionId)
        call.enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    callback?.onSuccessSessionDeleted()
                }else {
                    callback?.onFailedSessionDelete()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedSessionDelete()
            }
        })
    }

    private fun executeUpdateSessionSessionId(call: Call<ResponseBody>){
        call.enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    callback?.onSuccessSessionUpdate()
                }else {
                    callback?.onFailedSessionUpdate()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedSessionUpdate()
            }
        })
    }

    private fun executeAndParseVerificationOTP(call: Call<ResponseBody>){
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    val otp = response.body()!!.string()
                    callback?.onSuccessSubUserVerificationCodeSent(otp)
                }else{
                    callback?.onVerificationCodeFailed()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onVerificationCodeFailed()
            }
        })
    }

    private fun executeCreateUpdateSubUserAPICall(call: Call<ResponseBody>){
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    callback?.onCreateUpdateSubUserProfileResult(true)
                }else{
                    callback?.onCreateUpdateSubUserProfileResult(false)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onCreateUpdateSubUserProfileResult(false)
            }
        })
    }

    private fun executeAdminAPICall(query: String){
        var call = adminApi.getAdminsProfile(query)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    val responseString = response.body()!!.string()
                    val responseJson = Gson().fromJson(responseString, JsonObject::class.java)
                    val recordsArray = responseJson.get("records").asJsonArray
                    val adminProfiles = mutableListOf<AdminProfile>()
                    for (record in recordsArray) {
                        val recordArray = record.asJsonArray
                        val adminId = recordArray[0].asJsonObject.get("stringValue").asString
                        val email = recordArray[1].asJsonObject.get("stringValue").asString
                        val phone = recordArray[2].asJsonObject.get("stringValue").asString
                        val firstName = recordArray[3].asJsonObject.get("stringValue").asString
                        val lastName = recordArray[4].asJsonObject.get("stringValue").asString
                        val age = recordArray[5].asJsonObject.get("stringValue").asString
                        val gender = recordArray[6].asJsonObject.get("stringValue").asString
                        val weight = recordArray[7].asJsonObject.get("stringValue").asString
                        val height = recordArray[8].asJsonObject.get("stringValue").asString
                        val location = recordArray[9].asJsonObject.get("stringValue").asString
                        val profilePicUrl = recordArray[10].asJsonObject.get("stringValue").asString
                        val totalSessionsTaken = recordArray[11].asJsonObject.get("stringValue").asString
                        val totalUsersAdded = recordArray[12].asJsonObject.get("stringValue").asString
                        val adminProfile = AdminProfile(adminId, email, phone, firstName, lastName, age, gender, weight, height, location, profilePicUrl, totalSessionsTaken, totalUsersAdded)
                        loggedInUser = adminProfile
                        adminProfiles.add(adminProfile)
                    }
                    callback?.onSuccessAdminProfileResult(adminProfiles)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedAdminProfileResult("")
            }
        })
    }

    private fun executeDeleteSessionBYSessionID(sessionId: String){
        var call = adminApi.deleteSession(sessionId)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    callback?.onSingleSessionDeleted()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onSessionDeleteFailed()
            }
        })
    }

    private fun executeUpdateAdminProfilePic(adminProfile: AdminProfile){
        var call = adminApi.updateAdminProfilePic(adminProfile)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    callback?.onSuccessAdminProfilePicUpdated(adminProfile.profile_pic_url)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onAdminProfilePicUpdateFailed()
            }
        })
    }

    fun getEmailAndSendOTP(phone: String){
        executeGetEmailByPhone(phone)
    }

    private fun executeGetEmailByPhone(phone: String){
        var call = adminApi.getAdminProfileByPhone(phone)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    val responseString = response.body()!!.string()
                    val responseJson = Gson().fromJson(responseString, JsonObject::class.java)
                    val recordsArray = responseJson.get("records").asJsonArray
                    if(recordsArray.isEmpty){
                        callback?.onNoEmailFoundByPhone("No User Found")
                    }else{
                        if(!recordsArray.isEmpty){
                            val profile = recordsArray[0].asJsonArray
                            val email = profile[1].asJsonObject.get("stringValue").asString
                            callback!!.onSuccessfullyEmailFoundByPhone(email)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedAdminProfileResult("")
            }
        })
    }

    private fun executeAdminAPICallByPhone(phone: String, password: String){
        var call = adminApi.getAdminProfileByPhone(phone)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
               if(response.isSuccessful){
                   val responseString = response.body()!!.string()
                   val responseJson = Gson().fromJson(responseString, JsonObject::class.java)
                   val recordsArray = responseJson.get("records").asJsonArray
                   if(recordsArray.isEmpty){
                       callback?.onNoEmailFoundByPhone("No User Found")
                   }else{
                       val profile = recordsArray[0].asJsonArray
                       val email = profile[1].asJsonObject.get("stringValue").asString
                       callback!!.onSuccessfullyEmailFoundByPhone(email, password)
                   }
               }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedAdminProfileResult("")
            }
        })
    }

    private fun executeAdminSearchAPICall(query: String){
        var call = adminApi.searchSubUsersProfile(query)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if(response.isSuccessful){
                    val responseString = response.body()!!.string()
                    val responseJson = Gson().fromJson(responseString, JsonObject::class.java)
                    val recordsArray = responseJson.get("records").asJsonArray
                    val adminProfiles = mutableListOf<SubUserProfile>()
                    for (record in recordsArray) {
                        val recordArray = record.asJsonArray
                        val userId = recordArray[0].asJsonObject.get("stringValue").asString
                        val admin_id = recordArray[1].asJsonObject.get("stringValue").asString
                        val caretaker_id = recordArray[2].asJsonObject.get("stringValue").asString
                        val phone = recordArray[3].asJsonObject.get("stringValue").asString
                        val isUserVerified = recordArray[4].asJsonObject.get("booleanValue").asBoolean
                        val first_name = recordArray[5].asJsonObject.get("stringValue").asString
                        val last_name = recordArray[6].asJsonObject.get("stringValue").asString
                        val dob = recordArray[7].asJsonObject.get("stringValue").asString
                        val gender = recordArray[8].asJsonObject.get("stringValue").asString
                        val height = recordArray[9].asJsonObject.get("stringValue").asString
                        val location = recordArray[10].asJsonObject.get("stringValue").asString
                        val reminder = recordArray[11].asJsonObject.get("stringValue").asString
                        val profilePicUrl = recordArray[12].asJsonObject.get("stringValue").asString
                        val medicalHistory = recordArray[13].asJsonObject.get("stringValue").asString
                        val secAns = recordArray[14].asJsonObject.get("stringValue").asString
                        val chiefComplaint = recordArray[15].asJsonObject.get("stringValue").asString
                        val HPI_presentIllness = recordArray[16].asJsonObject.get("stringValue").asString
                        val FamilyHistory = recordArray[17].asJsonObject.get("stringValue").asString
                        val SocialHistory = recordArray[18].asJsonObject.get("stringValue").asString
                        val PastMedicalSurgicalHistory = recordArray[19].asJsonObject.get("stringValue").asString
                        val Medication = recordArray[20].asJsonObject.get("stringValue").asString
                        val country_code = recordArray[21].asJsonObject.get("stringValue").asString

                        val searchProfile = SubUserProfile(
                            userId,
                            admin_id,
                            caretaker_id,
                            phone,
                            isUserVerified,
                            first_name,
                            last_name,
                            dob,
                            gender,
                            height,
                            location,
                            reminder,
                            profilePicUrl,
                            medicalHistory,
                            secAns,
                            chiefComplaint,
                            HPI_presentIllness,
                            FamilyHistory,
                            SocialHistory,
                            PastMedicalSurgicalHistory,
                            Medication,
                            country_code
                        )

                        adminProfiles.add(searchProfile)
//                        val searchProfile = SubUserProfile(userId, phone, isUserVerified, firstName, lastName, dob, gender, height, location, profilePicUrl, medicalHistory, totalSessionsDone)
//                        adminProfiles.add(searchProfile)
                    }
                    callback?.onSearchSubUserProfileResult(adminProfiles)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedSearchProfileResult()
            }
        })
    }

    private fun executeGetSubUserProfile(call: Call<ResponseBody>){
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    val responseString = response.body()!!.string()
                    val responseJson = Gson().fromJson(responseString, JsonObject::class.java)
                    val recordsArray = responseJson.get("records").asJsonArray
                    val adminProfiles = mutableListOf<SubUserProfile>()
                    for (record in recordsArray) {
                        val recordArray = record.asJsonArray
                        val userId = recordArray[0].asJsonObject.get("stringValue").asString
                        val admin_id = recordArray[1].asJsonObject.get("stringValue").asString
                        val caretaker_id = recordArray[2].asJsonObject.get("stringValue").asString
                        val phone = recordArray[3].asJsonObject.get("stringValue").asString
                        val isUserVerified = recordArray[4].asJsonObject.get("booleanValue").asBoolean
                        val first_name = recordArray[5].asJsonObject.get("stringValue").asString
                        val last_name = recordArray[6].asJsonObject.get("stringValue").asString
                        val dob = recordArray[7].asJsonObject.get("stringValue").asString
                        val gender = recordArray[8].asJsonObject.get("stringValue").asString
                        val height = recordArray[8].asJsonObject.get("stringValue").asString
                        val location = recordArray[9].asJsonObject.get("stringValue").asString
                        val reminder = recordArray[10].asJsonObject.get("stringValue").asString
                        val profilePicUrl = recordArray[11].asJsonObject.get("stringValue").asString
                        val medicalHistory = recordArray[12].asJsonObject.get("stringValue").asString
                        val secAns = recordArray[13].asJsonObject.get("stringValue").asString
                        val chiefComplaint = recordArray[14].asJsonObject.get("stringValue").asString
                        val HPI_presentIllness = recordArray[15].asJsonObject.get("stringValue").asString
                        val FamilyHistory = recordArray[16].asJsonObject.get("stringValue").asString
                        val SocialHistory = recordArray[17].asJsonObject.get("stringValue").asString
                        val PastMedicalSurgicalHistory = recordArray[18].asJsonObject.get("stringValue").asString
                        val Medication = recordArray[19].asJsonObject.get("stringValue").asString
                        val country_code = recordArray[21].asJsonObject.get("stringValue").asString

                        val searchProfile = SubUserProfile(
                            userId,
                            admin_id,
                            caretaker_id,
                            phone,
                            isUserVerified,
                            first_name,
                            last_name,
                            dob,
                            gender,
                            height,
                            location,
                            reminder,
                            profilePicUrl,
                            medicalHistory,
                            secAns,
                            chiefComplaint,
                            HPI_presentIllness,
                            FamilyHistory,
                            SocialHistory,
                            PastMedicalSurgicalHistory,
                            Medication,
                            country_code
                        )

                        adminProfiles.add(searchProfile)
//                        val searchProfile = SubUserProfile(userId, phone, isUserVerified, firstName, lastName, dob, gender, height, location, profilePicUrl, medicalHistory, totalSessionsDone)
//                        adminProfiles.add(searchProfile)
                    }
                    callback?.onSubUserProfileFound(adminProfiles)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onSubUserProfileNotFound()
            }
        })
    }

    private fun executeDeleteUserSession(call: Call<ResponseBody>){
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    Log.d("TAG", "onResponse: delete suc")
                    callback?.onGuestSessionsDeleted()
                }else{
                    Log.d("TAG", "onResponse: delete err ${response.errorBody().toString()}")
                    callback?.onGuestSessionDeleteFailed()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG", "onFailure: ")
                callback?.onGuestSessionDeleteFailed()
            }
        })
    }

    private fun executeGetUserByPhone(call: Call<ResponseBody>){
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    val responseString = response.body()!!.string()
                    val responseJson = Gson().fromJson(responseString, JsonObject::class.java)
                    val recordsArray = responseJson.get("records").asJsonArray
                    if(recordsArray.isEmpty) {
                        callback?.userIsNotRegistered()
                    } else {
                        var isverified = false
                        for (record in recordsArray) {
                            val recordArray = record.asJsonArray
                            isverified = recordArray[4].asJsonObject.get("booleanValue").asBoolean
                        }
                        if(isverified){
                            callback?.userAllReadyRegistered()
                        }else{
                            callback?.userIsNotRegistered()
                        }
                    }
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG", "onFailure: ")
            }
        })
    }

    private fun executeGetSessionsByUserID(call: Call<ResponseBody>){
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    val responseString = response.body()!!.string()
                    val responseJson = Gson().fromJson(responseString, JsonObject::class.java)
                    val recordsArray = responseJson.get("records").asJsonArray
                    val userSessions = mutableListOf<Session>()
                    for (record in recordsArray) {
                        val recordArray = record.asJsonArray
                        val date = recordArray[0].asJsonObject.get("stringValue").asString
                        val time = recordArray[1].asJsonObject.get("stringValue").asString
                        val deviceId = recordArray[2].asJsonObject.get("stringValue").asString
                        val userId = recordArray[3].asJsonObject.get("stringValue").asString
                        val adminId = recordArray[4].asJsonObject.get("stringValue").asString
                        val sessionId = recordArray[5].asJsonObject.get("stringValue").asString
                        val sys = recordArray[6].asJsonObject.get("stringValue").asString
                        val dia = recordArray[7].asJsonObject.get("stringValue").asString
                        val heartRate = recordArray[8].asJsonObject.get("stringValue").asString
                        val spO2 = recordArray[9].asJsonObject.get("stringValue").asString
                        val weight = recordArray[10].asJsonObject.get("stringValue").asString
                        val bodyFat = recordArray[11].asJsonObject.get("stringValue").asString
                        val temp = recordArray[12].asJsonObject.get("stringValue").asString
                        val ecgFileLink = recordArray[13].asJsonObject.get("stringValue").asString
                        val PhysicalExamination = recordArray[14].asJsonObject.get("stringValue").asString
                        val LabotryRadiology= recordArray[15].asJsonObject.get("stringValue").asString
                        val ImpressionPlan= recordArray[16].asJsonObject.get("stringValue").asString
                        val questionerAnswers = recordArray[17].asJsonObject.get("stringValue").asString
                        val remarks = recordArray[18].asJsonObject.get("stringValue").asString
                        val location = recordArray[19].asJsonObject.get("stringValue").asString
                        val session = Session(date, time, deviceId, userId, adminId, sessionId, sys, dia, heartRate, spO2, weight, bodyFat, temp, ecgFileLink, PhysicalExamination, LabotryRadiology, ImpressionPlan, questionerAnswers, remarks, location)
                        userSessions.add(session)
                    }
                    callback?.onSuccessSubUserSessions(userSessions)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedSubUserSessions()
            }
        })
    }


    private fun executeUpdateFullSessionBySessionId(call: Call<ResponseBody>){
        call.enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    callback?.onSingleSessionUpdated()
                    Log.d("TAG", "onResponse: update ${response.body().toString()}")
                }else {
                    Log.d("TAG", "onResponse: update err ${response.errorBody().toString()}")
                    callback?.onFailedPostSession()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedPostSession()
            }
        })
    }


    private fun executeUpdateSessionRemarkBySessionId(call: Call<ResponseBody>){
        call.enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    callback?.onSuccessRemarkUpdate()
                    Log.d("TAG", "onResponse: update ${response.body().toString()}")
                }else {
                    Log.d("TAG", "onResponse: update err ${response.errorBody().toString()}")
                    callback?.onFailedRemarkUpdate()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedRemarkUpdate()
            }
        })
    }

    private fun executeCreateSession(call: Call<ResponseBody>){
        call.enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    callback?.onSuccessPostSession()
                }else {
                    callback?.onFailedPostSession()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback?.onFailedPostSession()
            }
        })
    }

    companion object{
        val shared = APIManager()
    }
}