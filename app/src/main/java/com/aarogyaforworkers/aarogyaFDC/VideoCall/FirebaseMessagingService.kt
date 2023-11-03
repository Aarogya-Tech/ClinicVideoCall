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
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.aarogyaforworkers.aarogyaFDC.Constants.Companion.CHANNEL_ID
import com.aarogyaforworkers.aarogyaFDC.Constants.Companion.CHANNEL_ID_MissedCall
import com.aarogyaforworkers.aarogyaFDC.DummyBroadcast
import com.aarogyaforworkers.aarogyaFDC.HangupBroadcast
import com.aarogyaforworkers.aarogyaFDC.R
import com.aarogyaforworkers.aarogyaFDC.VideoConferencing
import com.aarogyaforworkers.aarogyaFDC.composeScreens.fetchImageFromUrl
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

        fun cancelNotification()
        {
            notificationManager.cancel(notificationID!!)
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

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

        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="End Call")
        {
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

        notificationID = kotlin.random.Random.nextInt()

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
            callRepo.isCallAccepted=true
            return
        }

        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="End Call Callee")
        {
            if(VideoConferencing.callRepo.VideoConferenceContext != null && callRepo.isOnCallScreen){
                callRepo.isOnCallScreen=false
                callRepo.isCallAccepted=false
                VideoConferencing.callRepo.VideoConferenceContext!!.finishAndRemoveTask()
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
            callRepo.updateProfileBitmap()
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

        val vibrationPattern = longArrayOf(0, 100, 200, 300)

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
                .setVibrate(vibrationPattern)
                .setDefaults(0)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/zegocloudmp3"))
                .setOngoing(true)
                .setFullScreenIntent(dummyPendingIntent, true)
                .setTimeoutAfter(10000)
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
            .setVibrate(vibrationPattern)
//            .setFullScreenIntent(pendingIntent,true)
            .setCustomContentView(custumView)
            .setCustomBigContentView(custumView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setDefaults(0)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/zegocloudmp3"))
            .setOngoing(true)
            .setTimeoutAfter(10000)
            .build()

            notificationManager.notify(notificationID!!, notification)
        }
        if(callRepo.receiverToken.value=="" && callRepo.NoMissedCall.value==false)
        {
            callRepo.timerCallee.start()
        }
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
            setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/zegocloudmp3"),
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_RING)
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build())
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
}
//import android.app.KeyguardManager
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.PendingIntent.FLAG_ONE_SHOT
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.media.AudioAttributes
//import android.media.AudioFocusRequest
//import android.media.AudioManager
//import android.media.MediaPlayer
//import android.os.Build
//import android.os.Handler
//import android.os.Vibrator
//import android.provider.Settings
//import android.util.Log
//import android.widget.RemoteViews
//import androidx.annotation.RequiresApi
//import androidx.compose.ui.graphics.Color
//import androidx.core.app.NotificationCompat
//import com.aarogyaforworkers.aarogyaFDC.Constants.Companion.CHANNEL_ID
//import com.aarogyaforworkers.aarogyaFDC.HangupBroadcast
//import com.aarogyaforworkers.aarogyaFDC.R
//import com.aarogyaforworkers.aarogyaFDC.VideoConferencing
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import java.util.concurrent.TimeUnit
//
//
//class FirebaseMessagingService : FirebaseMessagingService(),MediaPlayer.OnPreparedListener {
//
//    companion object {
//
//        var sharedPref: SharedPreferences? = null
//
//        val callRepo = VideoConferencing.callRepo
//
//        var notificationID:Int?=null
//
//        lateinit var notificationManager: NotificationManager
//
//        var confrenceId: String? = null
//        var id: String?
//            get() {
//                return sharedPref?.getString("userId", "")
//            }
//            set(value) {
//                sharedPref?.edit()?.putString("userId", value)?.apply()
//            }
//        var token: String?
//            get() {
//                return sharedPref?.getString("token", "")
//            }
//            set(value) {
//                sharedPref?.edit()?.putString("token", value)?.apply()
//            }
//    }
//
//    var mediaPlayer: MediaPlayer? = null
//    var mvibrator: Vibrator? = null
//    var audioManager: AudioManager? = null
//    var playbackAttributes: AudioAttributes? = null
//    private var handler: Handler? = null
//    var afChangeListener: AudioManager.OnAudioFocusChangeListener? = null
//    private var status = false
//    private var vstatus = false
//
//
//    override fun onNewToken(newToken: String) {
//        super.onNewToken(newToken)
//        token = newToken
//        Log.e("FCM", "onMessageReceived: on New token $token")
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//
//        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.get("conferenceID")=="End Call") {
//            VideoConferencing.VideoConferenceContext.finishAndRemoveTask()
//            return
//        }
//
//        try {
//            audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
//            if (audioManager != null) {
//                when (audioManager!!.ringerMode) {
//                    AudioManager.RINGER_MODE_NORMAL -> status = true
//                    AudioManager.RINGER_MODE_SILENT -> status = false
//                    AudioManager.RINGER_MODE_VIBRATE -> {
//                        status = false
//                        vstatus = true
//                        Log.e("Service!!", "vibrate mode")
//                    }
//                }
//            }
//            if (status) {
//                val delayedStopRunnable = Runnable { releaseMediaPlayer() }
//                afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
//                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
//                        // Permanent loss of audio focus
//                        // Pause playback immediately
//                        //mediaController.getTransportControls().pause();
//                        if (mediaPlayer != null) {
//                            if (mediaPlayer!!.isPlaying()) {
//                                mediaPlayer!!.pause()
//                            }
//                        }
//                        // Wait 30 seconds before stopping playback
//                        handler!!.postDelayed(
//                            delayedStopRunnable,
//                            TimeUnit.SECONDS.toMillis(30)
//                        )
//                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
//                        // Pause playback
//                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
//                        // Lower the volume, keep playing
//                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
//                        // Your app has been granted audio focus again
//                        // Raise volume to normal, restart playback if necessary
//                    }
//                }
//                val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
//                mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
//                mediaPlayer!!.isLooping = true
//                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    handler = Handler()
//                    playbackAttributes = AudioAttributes.Builder()
//                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .build()
//                    val focusRequest =
//                        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
//                            .setAudioAttributes(playbackAttributes!!)
//                            .setAcceptsDelayedFocusGain(true)
//                            .setOnAudioFocusChangeListener(afChangeListener!!, handler!!)
//                            .build()
//                    val res = audioManager!!.requestAudioFocus(focusRequest)
//                    if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                        if (!keyguardManager.isDeviceLocked) {
//                            mediaPlayer!!.start()
//                        }
//                    }
//                } else {
//
//                    // Request audio focus for playback
//                    val result = audioManager!!.requestAudioFocus(
//                        afChangeListener,  // Use the music stream.
//                        AudioManager.STREAM_MUSIC,  // Request permanent focus.
//                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
//                    )
//                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                        if (!keyguardManager.isDeviceLocked) {
//                            // Start playback
//                            mediaPlayer!!.start()
//                        }
//                    }
//                }
//            } else if (vstatus) {
//                mvibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
//                // Start without a delay
//                // Each element then alternates between vibrate, sleep, vibrate, sleep...
//                val pattern = longArrayOf(
//                    0, 250, 200, 250, 150, 150, 75,
//                    150, 75, 150
//                )
//
//                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
//                mvibrator!!.vibrate(pattern, 0)
//                Log.e("Service!!", "vibrate mode start")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        if(remoteMessage.data.isNotEmpty()){
//            val data = remoteMessage.data.values.first()
//            val splitText = data.split("-:-")
//            callRepo.updateConfrenceId(splitText.first())
//            confrenceId = splitText.first()
//            callRepo.updateReceiverName(splitText[1])
//            callRepo.updateReceiverClinicName(splitText[2])
//            callRepo.updateReceiverProfileUrl(splitText.last())
//            Log.d("TAG", "onMessageReceived: notification data $splitText")
//        }
//
//        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//
//        if (audioManager != null) {
//            when (audioManager!!.ringerMode) {
//                AudioManager.RINGER_MODE_NORMAL -> status = true
//                AudioManager.RINGER_MODE_SILENT -> status = false
//                AudioManager.RINGER_MODE_VIBRATE -> {
//                    status = false
//                    vstatus = true
//                    Log.e("Service!!", "vibrate mode")
//                }
//            }
//        }
//
//        val custumView= RemoteViews(packageName, R.layout.custom_call_notification)
//
//        val notificationIntent= Intent(this, VideoConferencing::class.java)
//
//        val hangupIntent= Intent(this, HangupBroadcast::class.java)
//        hangupIntent.action = "ACTION_REJECT"
//
//        val answerIntent= Intent(this, VideoConferencing::class.java)
//        answerIntent.action = "ACTION_ACCEPT"
//
//
//        custumView.setTextViewText(R.id.name,"Incoming Call from " + callRepo.receiverName.value)
//
//        custumView.setTextViewText(R.id.CallType,callRepo.receiverClinicName.value)
//
//
//        val pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//
//
//        val hangupPendingIntent=PendingIntent.getBroadcast(this,0,hangupIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//
//
//        val answerPendingIntent=PendingIntent.getActivity(this,0,answerIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
//
//
//        custumView.setOnClickPendingIntent(R.id.btnAccept,answerPendingIntent)
//
//        custumView.setOnClickPendingIntent(R.id.btnDecline,hangupPendingIntent)
//
//        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationID = kotlin.random.Random.nextInt()
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel(notificationManager)
//        }
//
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(callRepo.receiverClinicName.value)
//            .setContentText("Call from ${callRepo.receiverName.value}")
//            .setSmallIcon(android.R.mipmap.sym_def_app_icon)
//            .setAutoCancel(true)
//            .setCategory(NotificationCompat.CATEGORY_CALL)
//            .setFullScreenIntent(pendingIntent,true)
//            .setCustomContentView(custumView)
//            .setCustomBigContentView(custumView)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setDefaults(0)
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
////            .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/zegocloudmp3"))
//            .setOngoing(true)
//            .build()
//
//        notificationManager.notify(notificationID!!, notification)
//
//        super.onMessageReceived(remoteMessage)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(notificationManager: NotificationManager) {
//        val channelName = "Call Invitation"
//        val channel = NotificationChannel(
//            CHANNEL_ID,
//            channelName,
//            NotificationManager.IMPORTANCE_HIGH
//        ).apply {
//            description = "Video Call Invitation"
//            enableLights(true)
//            lightColor = Color.Cyan.hashCode()
////            setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/zegocloudmp3"),
////                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
////                    .setLegacyStreamType(AudioManager.STREAM_RING)
////                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build())
//            lockscreenVisibility = Notification.VISIBILITY_PUBLIC;
//        }
//        notificationManager.createNotificationChannel(channel)
//    }
//
//    override fun onPrepared(p0: MediaPlayer?) {
//
//    }
//
//    fun releaseVibration() {
//        try {
//            if (mvibrator != null) {
//                if (mvibrator!!.hasVibrator()) {
//                    mvibrator!!.cancel()
//                }
//                mvibrator = null
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun releaseMediaPlayer() {
//        try {
//            if (mediaPlayer != null) {
//                if (mediaPlayer!!.isPlaying) {
//                    mediaPlayer!!.stop()
//                    mediaPlayer!!.reset()
//                    mediaPlayer!!.release()
//                }
//                mediaPlayer = null
//            }
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy() // release your media player here audioManager.abandonAudioFocus(afChangeListener);
//        releaseMediaPlayer()
//        releaseVibration()
//    }
//}