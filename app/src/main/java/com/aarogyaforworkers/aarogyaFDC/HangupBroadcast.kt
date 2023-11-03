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

        Log.i("TAG","Missed Call11")
        FirebaseMessagingService.notificationManager.cancel(FirebaseMessagingService.notificationID!!)

        // Cancel the call on the caller side ->

        if(p1 != null && p1.type != null){
            if(p1.type!!.isNotEmpty()){
                Log.d("TAG", "onReceive: notification cancel ${p1.type}")
                FirebaseMessagingService.callRepo.sendCancelCallNotificationToCaller(p1.type!!)
            }
        }
    }

}