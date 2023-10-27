package com.aarogyaforworkers.aarogyaFDC.VideoCall

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import com.aarogyaforworkers.aarogyaFDC.Constants.Companion.CHANNEL_ID
import com.aarogyaforworkers.aarogyaFDC.HangupBroadcast
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.math.log

class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        var sharedPref: SharedPreferences? = null

        val callRepo = VideoConferencing.callRepo

        var notificationID:Int?=null

        lateinit var notificationManager: NotificationManager

        var confrenceId: String? = null
        var id: String?
            get() {
                return sharedPref?.getString("userId", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("userId", value)?.apply()
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

        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="End Call") {

            Log.d("TAG", "onMessageReceived: notification is on call screen ${callRepo.isOnCallScreen}")

            callRepo.isOnCallScreen = false

            if(notificationID != null){
                notificationManager.cancel(notificationID!!)
            }

            if(VideoConferencing.VideoConferenceContext != null){
                VideoConferencing.VideoConferenceContext!!.finishAndRemoveTask()
            }
            return
        }
        if(remoteMessage.data.isNotEmpty()){
            val data = remoteMessage.data.values.first()
            val splitText = data.split("-:-")
            callRepo.updateConfrenceId(splitText.first())
            confrenceId = splitText.first()
            callRepo.updateReceiverName(splitText[1])
            callRepo.updateReceiverClinicName(splitText[2])
            callRepo.updateReceiverProfileUrl(splitText[3])
            callRepo.updateReceiverToken(splitText.last())
            Log.d("TAG", "onMessageReceived: notification data $splitText")
        }

        val custumView= RemoteViews(packageName, R.layout.custom_call_notification)

        val notificationIntent= Intent(this, VideoConferencing::class.java)

        val hangupIntent= Intent(this, HangupBroadcast::class.java)
        hangupIntent.action = "ACTION_REJECT"
        hangupIntent.setType(callRepo.receiverToken.value)

        val answerIntent= Intent(this, VideoConferencing::class.java)
        answerIntent.action = "ACTION_ACCEPT"


        custumView.setTextViewText(R.id.name,"Incoming Call from " + callRepo.receiverName.value)

        custumView.setTextViewText(R.id.CallType,callRepo.receiverClinicName.value)


        val pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)


        val hangupPendingIntent=PendingIntent.getBroadcast(this,0,hangupIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)


        val answerPendingIntent=PendingIntent.getActivity(this,0,answerIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)


        custumView.setOnClickPendingIntent(R.id.btnAccept,answerPendingIntent)

        custumView.setOnClickPendingIntent(R.id.btnDecline,hangupPendingIntent)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("TAG", "Message data payload: ${remoteMessage.data}")
        }

        super.onMessageReceived(remoteMessage)

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationID = kotlin.random.Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val vibrationPattern = longArrayOf(0, 100, 200, 300) // Vibrate for 100ms, pause for 200ms, vibrate for 300ms, and so on

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(callRepo.receiverClinicName.value)
            .setContentText("Call from ${callRepo.receiverName.value}")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVibrate(vibrationPattern)
            .setFullScreenIntent(pendingIntent,true)
            .setCustomContentView(custumView)
            .setCustomBigContentView(custumView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .build()

        notificationManager.notify(notificationID!!, notification)
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