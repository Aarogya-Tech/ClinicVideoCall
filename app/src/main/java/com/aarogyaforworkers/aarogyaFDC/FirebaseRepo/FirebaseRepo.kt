package com.aarogyaforworkers.aarogyaFDC.FirebaseRepo

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.storage.ProfilePreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class FirebaseRepo {

    private var isLatestToken : MutableState<String?> = mutableStateOf(null)

    var latestToken : State<String?> = isLatestToken

    fun updateLatestToken(token : String?){
        isLatestToken.value = token
    }

    private var isTokenUpdated : MutableState<Boolean?> = mutableStateOf(null)

    var tokenUpdateState : State<Boolean?> = isTokenUpdated

    fun updateTokenState(tokenState : Boolean?){
        isTokenUpdated.value = tokenState
    }

    fun saveTokenLocal(context : Context){
        val pLocal =  ProfilePreferenceManager.getInstance(context)
        if(latestToken.value != null) {
            pLocal.saveToken(latestToken.value!!)
        }
    }

    fun getToken(context: Context){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                updateLatestToken(null)
                Log.e("FCM", "onMessageReceived: Fetching FCM registration token failed")
                return@OnCompleteListener
            }
            val token = task.result
            updateLatestToken(token)
            val pLocal =  ProfilePreferenceManager.getInstance(context)
            if(pLocal.getToken() != token){
                Log.e("FCM", "onMessageReceived: Updating token $token")
                pLocal.saveToken(token)
                updateFCMTokenInProfile(token)
                MainActivity.callRepo.sendsetUpCallNotification(token)
            }
        })
    }

    fun deleteToken(){
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
            Log.d("TAG", "deleteToken: ${it.isSuccessful}")
        }
    }

    private fun updateFCMTokenInProfile(token : String){
        val doctor = MainActivity.adminDBRepo.adminProfileState.value
        doctor.token = token
        MainActivity.adminDBRepo.updateAdminProfileToken(doctor)
    }

    companion object {

        // Singleton instantiation you already know and love
        @Volatile private var instance: FirebaseRepo? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FirebaseRepo().also { instance = it }
            }
    }

}