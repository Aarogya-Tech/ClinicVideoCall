package com.aarogyaforworkers.aarogyaFDC

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


private const val CHANNEL_ID = "CallInvitation"

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        var sharedPref: SharedPreferences? = null

        var isfromnotification: Boolean?
            get() {
                return sharedPref?.getBoolean("notification", false)
            }
            set(value) {
                sharedPref?.edit()?.putBoolean("notification", value!!)?.apply()
            }

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
        Log.e("FCM", "onMessageReceived: on New token $token")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val custumView=RemoteViews(packageName,com.aarogyaforworkers.aarogyaFDC.R.layout.custom_call_notification)

        val notificationIntent=Intent(this,VideoConferencing::class.java)
        val hangupIntent=Intent(this,VideoConferencing::class.java)
        val answerIntent=Intent(this,VideoConferencing::class.java)

        custumView.setTextViewText(com.aarogyaforworkers.aarogyaFDC.R.id.name,"Aarogya Clinic")

        val pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,
            FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val hangupPendingIntent=PendingIntent.getActivity(this,0,hangupIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val answerPendingIntent=PendingIntent.getActivity(this,0,answerIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        custumView.setOnClickPendingIntent(com.aarogyaforworkers.aarogyaFDC.R.id.btnAccept,answerPendingIntent)
        custumView.setOnClickPendingIntent(com.aarogyaforworkers.aarogyaFDC.R.id.btnDecline,hangupPendingIntent)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("TAG", "Message data payload: ${remoteMessage.data}")
        }

        super.onMessageReceived(remoteMessage)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = kotlin.random.Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Aarogya Clinic")
            .setContentText("Call Invitation")
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(pendingIntent,true)
            .setCustomContentView(custumView)
            .setCustomBigContentView(custumView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()

        notificationManager.notify(notificationID, notification)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "Call Invitation"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "Video Call Invitation"
            enableLights(true)
            lightColor = Color.Cyan.hashCode()
        }
        notificationManager.createNotificationChannel(channel)
    }
}

