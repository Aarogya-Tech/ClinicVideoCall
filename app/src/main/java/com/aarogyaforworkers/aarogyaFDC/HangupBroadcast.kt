package com.aarogyaforworkers.aarogyaFDC

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aarogyaforworkers.aarogyaFDC.VideoCall.FirebaseMessagingService
import com.aarogyaforworkers.aarogyaFDC.VideoCall.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HangupBroadcast: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        FirebaseMessagingService.notificationManager.cancel(FirebaseMessagingService.notificationID!!)
//       Log.i("TAG","REJECT")
        PushNotification(
            "fenwa43GRfS11wZkLhVeK-:APA91bEtiwzFJ5Fy45BzJnd7hJGVB86oC7LwocakjU0xICRu5_UV3RXfsggWABr-obNzsrCX-FkeXIwyg5aCvBrxm2bXLh5pwjF2jUr620RTRqbNpkGrrNHrUYcvrwWn2b-3uZnjVpH7",
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