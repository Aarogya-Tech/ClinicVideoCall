package com.aarogyaforworkers.aarogyaFDC.VideoCall

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.aarogyaforworkers.aarogyaFDC.Constants.Companion.CHANNEL_ID
import com.aarogyaforworkers.aarogyaFDC.Constants.Companion.CHANNEL_ID_MissedCall
import com.aarogyaforworkers.aarogyaFDC.DummyBroadcast
import com.aarogyaforworkers.aarogyaFDC.HangupBroadcast
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.R
import com.aarogyaforworkers.aarogyaFDC.VideoConferencing
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        var sharedPref: SharedPreferences? = null

        val callRepo = VideoConferencing.callRepo

        var notificationID:Int?=null

        var notificationIDMissed:Int?=null

        lateinit var notificationManager: NotificationManager

        lateinit var notificationManagerMissed: NotificationManager

        lateinit var context:Context

        var confrenceId: String? = null
        var name: String?
            get() {
                return sharedPref?.getString("userName", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("userName", value)?.apply()
            }
        var token: String?=""
//            get() {
//                return sharedPref?.getString("token", "")
//            }
//            set(value) {
//                sharedPref?.edit()?.putString("token", value)?.apply()
//            }

        fun cancelNotification()
        {
            notificationManager.cancel(notificationID!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {


        Log.i("TAG",remoteMessage.senderId.toString())
        Log.i("TAG",remoteMessage.data.values.first())

        context=this

        if(notificationID==null)
        {
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationID = kotlin.random.Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(
                    notificationManager,
                )
            }
        }

        if(notificationIDMissed==null)
        {
            notificationManagerMissed = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationIDMissed = kotlin.random.Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannelMissed(
                    notificationManagerMissed
                )
            }
        }

        Log.i("TAG","insideOnMessageRecieved")

        notificationIDMissed = kotlin.random.Random.nextInt()

        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="End Call") {
            Log.i("TAG",VideoConferencing.callRepo.VideoConferenceContext.toString())
            if(VideoConferencing.callRepo.VideoConferenceContext != null && callRepo.isOnCallScreen ){
                VideoConferencing.callRepo.VideoConferenceContext!!.finishAndRemoveTask()
                callRepo.isOnCallScreen = false
                return
            }

            if(!callRepo.isOnCallScreen){

                Log.i("TAG","Missed Call1")
                notificationManager.cancel(notificationID!!)

                val notification = NotificationCompat.Builder(this, CHANNEL_ID_MissedCall)
                    .setContentTitle(callRepo.receiverClinicName.value)
                    .setContentText("Missed Call from ${callRepo.receiverName.value}")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setAutoCancel(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setSound(null)
                    .setDefaults(0)
                    .setVibrate(null)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .build()
                notificationManagerMissed.notify(notificationIDMissed!!, notification)
                return
            }
        }

        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="End Calls") {

            callRepo.timerCallee.cancel()
            if(!callRepo.isOnCallScreen && !callRepo.NoMissedCall.value!!){
                Log.i("TAG","Missed Call2")
                notificationManager.cancel(notificationID!!)
                val notification = NotificationCompat.Builder(this, CHANNEL_ID_MissedCall)
                    .setContentTitle(callRepo.receiverClinicName.value)
                    .setContentText("Missed Call from ${callRepo.receiverName.value}")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setAutoCancel(false)
                    .setSound(null)
                    .setDefaults(0)
                    .setVibrate(null)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .build()
                notificationManagerMissed.notify(notificationIDMissed!!, notification)
            }
            else
                callRepo.updateNoMissedCall(false)
            return
        }

//        if(remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="Missed Call")
//        {
//            if(!callRepo.isOnCallScreen){
//                Log.i("TAG","Missed Call15"+ notificationManagerMissed.toString()+ notificationIDMissed.toString())
//                notificationManager.cancel(notificationID!!)
//                val notification = NotificationCompat.Builder(this, CHANNEL_ID_MissedCall)
//                    .setContentTitle(callRepo.receiverClinicName.value)
//                    .setContentText("Missed Call from ${callRepo.receiverName.value}")
//                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
//                    .setAutoCancel(false)
//                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                    .setCategory(NotificationCompat.CATEGORY_CALL)
//                    .setSound(null)
//                    .setDefaults(0)
//                    .setVibrate(null)
//                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//                    .build()
//                notificationManagerMissed.notify(notificationIDMissed!!, notification)
//            }
//
//            return
//        }

        if(remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="Accept Call")
        {
            VideoConferencing.mediaPlayer!!.stop()
            if(callRepo.isCallAccepted==false)
            {
                callRepo.isCallAccepted=true
            }
            return
        }

        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="End Call Callee")
        {
            VideoConferencing.mediaPlayer!!.stop()
            if(VideoConferencing.callRepo.VideoConferenceContext != null && callRepo.isOnCallScreen){
                callRepo.isOnCallScreen=false
                callRepo.isCallAccepted=false
                VideoConferencing.callRepo.VideoConferenceContext!!.finishAndRemoveTask()
            }
            return
        }

        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="Busy Call Callee")
        {
            if(VideoConferencing.callRepo.VideoConferenceContext != null && callRepo.isOnCallScreen)
            {
                if(callRepo.selectedCallersProfile.value.size==1){
                    VideoConferencing.mediaPlayer!!.stop()
                    callRepo.isOnCallScreen=false
                    callRepo.isCallAccepted=false
                    Handler(Looper.getMainLooper()).postDelayed({
                        VideoConferencing.callRepo.VideoConferenceContext!!.finishAndRemoveTask()
                        Toast.makeText(context,remoteMessage.data.get("token")+" is Busy",Toast.LENGTH_LONG).show()
                    },1000)
                }
                else{
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context,remoteMessage.data.get("token")+" is Busy",Toast.LENGTH_LONG).show()
                    }
                }
            }
            return
        }

        if(callRepo.isOnCallScreen || isNotificationActive())
        {
            sharedPref=context.getSharedPreferences("UserName", Context.MODE_PRIVATE)
            if(remoteMessage.data.isNotEmpty()) {
                val data = remoteMessage.data.values.first()
                val splitText = data.split("-:-")
                Log.d("TAG", "onMessageReceived: $splitText")
                val notification = NotificationCompat.Builder(this, CHANNEL_ID_MissedCall)
                    .setContentTitle(splitText[2])
                    .setContentText("Missed Call from ${splitText[1]}")
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setAutoCancel(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .setSound(null)
                    .setDefaults(0)
                    .setVibrate(null)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .build()
                notificationManagerMissed.notify(notificationIDMissed!!, notification)
            }
            callRepo.updateNoMissedCall(true);
            callRepo.sendBusyCallNotificationToCaller(remoteMessage.data.get("token")!!, name)
            return
        }

        notificationID = kotlin.random.Random.nextInt()

        if(remoteMessage.data.isNotEmpty()){
            val data = remoteMessage.data.values.first()
            val splitText = data.split("-:-")
            callRepo.updateConfrenceId(splitText.first())
            confrenceId = splitText.first()
            callRepo.updateReceiverName(splitText[1])
            callRepo.updateReceiverClinicName(splitText[2])
            callRepo.updateReceiverProfileUrl(splitText[3])
            callRepo.updateReceiverToken(splitText.last())
            callRepo.updateProfileBitmap()
            if(callRepo.receiverToken.value=="" && remoteMessage.data.get("token")!="")
            {
                token = remoteMessage.data.get("token")
            }
            Log.d("TAG", "onMessageReceived: notification data $splitText")
        }

        val custumView= RemoteViews(packageName, R.layout.custom_call_notification)

        val hangupIntent= Intent(this, HangupBroadcast::class.java)
        hangupIntent.action = "ACTION_REJECT"
        hangupIntent.setType(callRepo.receiverToken.value)

        val answerIntent= Intent(this, VideoConferencing::class.java)
        answerIntent.action = "ACTION_ACCEPT"

        val dummyIntent= Intent(this, DummyBroadcast::class.java)
        dummyIntent.action = "DUMMY_ACTION"


        custumView.setTextViewText(R.id.name,callRepo.receiverClinicName.value)

        custumView.setTextViewText(R.id.CallType,"Incoming Call from " + callRepo.receiverName.value)

        if(callRepo.profileBitmap.value==null)
        {
            Log.i("","")
        }
        else
        {
            custumView.setImageViewBitmap(R.id.photo, callRepo.profileBitmap.value)
        }

        val hangupPendingIntent=PendingIntent.getBroadcast(this,0,hangupIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val answerPendingIntent=PendingIntent.getActivity(this,0,answerIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val dummyPendingIntent=PendingIntent.getBroadcast(this,0,dummyIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        custumView.setOnClickPendingIntent(R.id.btnAccept,answerPendingIntent)

        custumView.setOnClickPendingIntent(R.id.btnDecline,hangupPendingIntent)

        val vibrationPattern = longArrayOf(
            0, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500
        )

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.S)
        {
            val incomingCaller = androidx.core.app.Person.Builder()
                .setName(callRepo.receiverName.value)
                .setIcon(if(callRepo.profileBitmap.value==null) null
                else IconCompat.createWithBitmap(callRepo.profileBitmap.value!!))
                .setImportant(true)
                .build()

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(callRepo.receiverClinicName.value)
                .setContentText("Call from ${callRepo.receiverName.value}")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setStyle(NotificationCompat.CallStyle.forIncomingCall(incomingCaller, hangupPendingIntent, answerPendingIntent))
                .addPerson(incomingCaller)
//                .setVibrate(vibrationPattern)
                .setDefaults(0)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setSound(null)
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ packageName +"/raw/" + R.raw.zegocloudmp3))
                .setOngoing(true)
                .setFullScreenIntent(dummyPendingIntent, true)
                .setTimeoutAfter(120000)
                .build()
            notificationManager.notify(notificationID!!, notification)
        }
        else{
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(callRepo.receiverClinicName.value)
                .setContentText("Call from ${callRepo.receiverName.value}")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
//            .setVibrate(vibrationPattern)
//            .setFullScreenIntent(pendingIntent,true)
                .setCustomContentView(custumView)
                .setCustomBigContentView(custumView)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setDefaults(0)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//                .setSound(null)
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName +"/raw/" + R.raw.zegocloudmp3))
                .setOngoing(true)
                .setTimeoutAfter(120000)
                .build()

            notificationManager.notify(notificationID!!, notification)
        }
        if(callRepo.receiverToken.value=="" && callRepo.NoMissedCall.value==false)
        {
            callRepo.timerCallee.start()
            //start call
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val vibrationPattern = longArrayOf(
            0, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500,
            100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500
        )
        val channelName = "Call Invitation"
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Video Call Invitation"
            enableLights(true)
            lightColor = Color.Cyan.hashCode()
            setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName +"/raw/" + R.raw.zegocloudmp3),
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_RING)
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build())
//            enableVibration(true)
//            setVibrationPattern(vibrationPattern)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC;
        }
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelMissed(notificationManager_missed: NotificationManager)
    {
        val channelName_missed = "Call Invitation_missed"
        val channel2 = NotificationChannel(
            CHANNEL_ID_MissedCall,
            channelName_missed,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Video Call Invitation"
            enableLights(true)
            lightColor = Color.Cyan.hashCode()
        }
        notificationManager_missed.createNotificationChannel(channel2)
    }

    fun isNotificationActive(): Boolean {
        val activeNotifications = notificationManager.activeNotifications
        Log.i("TAG",activeNotifications.size.toString()+activeNotifications)
        for (statusBarNotification in activeNotifications) {
            Log.i("TAG","Notification ID "+statusBarNotification.id+ notificationID)
            if (statusBarNotification.id == notificationID) {
                return true
            }
        }
        return false
    }
}