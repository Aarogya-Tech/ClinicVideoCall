package com.aarogyaforworkers.aarogyaFDC

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aarogyaforworkers.aarogyaFDC.VideoCall.FirebaseMessagingService
import com.aarogyaforworkers.aarogyaFDC.VideoCall.RetrofitInstance
import com.aarogyaforworkers.aarogyaFDC.composeScreens.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HangupBroadcast: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        FirebaseMessagingService.callRepo.updateNoMissedCall(true)

        FirebaseMessagingService.notificationManager.cancel(FirebaseMessagingService.notificationID!!)

        // Cancel the call on the caller side ->

        if(p1 != null && p1.type != null){
            if(p1.type!!.isNotEmpty()){
                Log.d("TAG", "onReceive: notification cancel ${p1.type}")
                sendCancelCallNotification(p1.type!!)
            }
        }

    }

    fun sendCancelCallNotification(token : String){
        PushNotification(
            token,
            Data("End Call")
        ).also {
            sendNotification(it)
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("TAG", "Response: $response")
            } else {
                Log.e("TAG", response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }

}