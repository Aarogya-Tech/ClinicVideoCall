package com.aarogyaforworkers.aarogyaFDC.VideoCall

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import com.aarogyaforworkers.aarogyaFDC.Constants.Companion.CHANNEL_ID
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.math.log

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        var sharedPref: SharedPreferences? = null

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

        val callRepo = MainActivity.callRepo

        if(remoteMessage.data.isNotEmpty()){
            val data = remoteMessage.data.values.first()
            val splitText = data.split("-:-")
            callRepo.updateConfrenceId(splitText.first())
            callRepo.updateReceiverName(splitText[1])
            callRepo.updateReceiverClinicName(splitText[2])
            callRepo.updateReceiverProfileUrl(splitText.last())
            Log.d("TAG", "onMessageReceived: notification data $splitText")
        }

        val custumView= RemoteViews(packageName, R.layout.custom_call_notification)

        val notificationIntent= Intent(this, VideoConferencing::class.java)
        val hangupIntent= Intent(this, VideoConferencing::class.java)
        val answerIntent= Intent(this, VideoConferencing::class.java)

        custumView.setTextViewText(R.id.name,"Incoming Call from " + callRepo.receiverName.value)
        custumView.setTextViewText(R.id.CallType,callRepo.receiverClinicName.value)

        val pendingIntent= PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val hangupPendingIntent= PendingIntent.getActivity(
            this,
            0,
            hangupIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val answerPendingIntent= PendingIntent.getActivity(
            this,
            0,
            answerIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        custumView.setOnClickPendingIntent(R.id.btnAccept,answerPendingIntent)

        custumView.setOnClickPendingIntent(R.id.btnDecline,hangupPendingIntent)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("TAG", "Message data payload: ${remoteMessage.data}")
        }

        super.onMessageReceived(remoteMessage)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = kotlin.random.Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(callRepo.receiverClinicName.value)
            .setContentText("Call from ${callRepo.receiverName.value}")
            .setSmallIcon(android.R.mipmap.sym_def_app_icon)
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
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Video Call Invitation"
            enableLights(true)
            lightColor = Color.Cyan.hashCode()
        }
        notificationManager.createNotificationChannel(channel)
    }
}