package com.aarogyaforworkers.aarogyaFDC

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class HangupBroadcast: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        FirebaseMessagingService.notificationManager.cancel(FirebaseMessagingService.notificationID!!)
       Log.i("TAG","REJECT")
    }

}