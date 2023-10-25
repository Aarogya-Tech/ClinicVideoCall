package com.aarogyaforworkers.aarogyaFDC

import android.R
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.BuildConfig
import com.google.firebase.messaging.FirebaseMessaging
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val custumView=RemoteViews(packageName,com.aarogyaforworkers.aarogyaFDC.R.layout.custom_call_notification)

        val notificationIntent=Intent(this,VideoConferencing::class.java)
        val hangupIntent=Intent(this,VideoConferencing::class.java)
        val answerIntent=Intent(this,VideoConferencing::class.java)

        custumView.setTextViewText(com.aarogyaforworkers.aarogyaFDC.R.id.name,"Aarogya Clinic")

        val pendingIntent=PendingIntent.getActivity(this,0,notificationIntent, FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val hangupPendingIntent=PendingIntent.getActivity(this,0,hangupIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val answerPendingIntent=PendingIntent.getActivity(this,0,answerIntent,FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        custumView.setOnClickPendingIntent(com.aarogyaforworkers.aarogyaFDC.R.id.btnAccept,answerPendingIntent)
        custumView.setOnClickPendingIntent(com.aarogyaforworkers.aarogyaFDC.R.id.btnDecline,hangupPendingIntent)

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel=NotificationChannel("IncomingCall","IncomingCall",NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.setSound(null,null)

        notificationManager.createNotificationChannel(notificationChannel)
        val notification=NotificationCompat.Builder(this,"IncomingCall")
        notification.setContentTitle("Aarogya Clinic")
        notification.setTicker("Call_STATUS")
        notification.setContentText("Incoming Call")
        notification.setSmallIcon(R.drawable.sym_def_app_icon)
        notification.setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
        notification.setCategory(NotificationCompat.CATEGORY_CALL)
        notification.setVibrate(null)
        notification.setOngoing(true)
        notification.setFullScreenIntent(pendingIntent,true)

        notification.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        notification.setCustomContentView(custumView)
        notification.setCustomBigContentView(custumView)

        startForeground(1124,notification.build())




//        MainActivity.zegoCloudViewModel.isfromNotification.value = true
//
//        isfromnotification=true
//
//
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d("TAG", "Message data payload: ${remoteMessage.data}")
//        }
//
//        super.onMessageReceived(remoteMessage)
//
//        val resultIntent = Intent(this, VideoConferencing::class.java)
//        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
//            addNextIntentWithParentStack(resultIntent)
//            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//        }
//
////        val intent = Intent(this, VideoConferencing::class.java)
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val notificationID = kotlin.random.Random.nextInt()
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel(notificationManager)
//        }
//
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
////            FLAG_UPDATE_CURRENT)
//
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(remoteMessage.notification!!.title)
//            .setContentText(remoteMessage.notification!!.body)
//            .setSmallIcon(R.mipmap.sym_def_app_icon)
//            .setAutoCancel(true)
//            .setContentIntent(resultPendingIntent)
//            .build()
//
//        notificationManager.notify(notificationID, notification)
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(notificationManager: NotificationManager) {
//        val channelName = "Call Invitation"
//        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
//            description = "Video Call Invitation"
//            enableLights(true)
//            lightColor = Color.Cyan.hashCode()
//        }
//        notificationManager.createNotificationChannel(channel)
//    }
}







///*
//    This class is from FCM quick start example
//    https://github.com/firebase/quickstart-android/blob/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm/java/MyFirebaseMessagingService.java
//
//    this service is automatically started by the SDK
//    https://stackoverflow.com/questions/43128290/android-notification-with-fcm-who-started-the-firebasemessagingservice
// */
//class FirebaseMessagingService : FirebaseMessagingService() {
//    // https://stackoverflow.com/questions/12172092/check-if-activity-is-running-from-service
//
//    fun isForeground(myPackage: String): Boolean {
//        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
//        val runningTaskInfo = manager.getRunningTasks(1)
//        val componentInfo = runningTaskInfo[0].topActivity
//        return componentInfo!!.packageName == myPackage
//    }
//
//    /**
//     * Called when message is received. AND when the app is in foreground (see the chart in link)
//     * https://firebase.google.com/docs/cloud-messaging/android/receive#handling_messages
//     * system will not automatically display a notification
//     * but we can manually create a notification in android
//     *
//     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
//     */
//    // [START receive_message]
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        // [START_EXCLUDE]
//        // There are two types of messages data messages and notification messages. Data messages
//        // are handled
//        // here in onMessageReceived whether the app is in the foreground or background. Data
//        // messages are the type
//        // traditionally used with GCM. Notification messages are only received here in
//        // onMessageReceived when the app
//        // is in the foreground. When the app is in the background an automatically generated
//        // notification is displayed.
//        // When the user taps on the notification they are returned to the app. Messages
//        // containing both notification
//        // and data payloads are treated as notification messages. The Firebase console always
//        // sends notification
//        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
//        // [END_EXCLUDE]
//
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//
//        // Check if message contains a data payload.
//        val dataPayLoad = remoteMessage.data
//        val extras = Bundle().apply {
//            dataPayLoad.forEach { (key, value) ->
//                putString(key, value)
//            }
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.notification != null) {
//            val body = remoteMessage.notification!!.body
//            extras.putString("notificationPayload", body)
//        }
//
//
//        // start the app from the background
//        // Is there a way to bring an application to foreground on push notification receive?
//        // https://stackoverflow.com/questions/51393431/is-there-a-way-to-bring-an-application-to-foreground-on-push-notification-receiv
//
//
//        // How to launch main Activity from Notification in a service library without hard coding Activity class
//        // https://stackoverflow.com/questions/13557654/how-to-launch-main-activity-from-notification-in-a-service-library-without-hard
//        // This block is for deciding what should the click action do
//        val startAppIntent = Intent()
//        val ctx = applicationContext
//        val mPackage = ctx.packageName
//        val mClass = "MainActivity"
//        // R.string.main_activity_name will be overridden by the module using this library
//        startAppIntent.component = ComponentName(mPackage, mClass)
//        val activityClass: Class<*> = try {
//            Class.forName("$mPackage.$mClass") // "com.aitmed.testpage.MainActivity"
//        } catch (e: ClassNotFoundException) {
//            e.printStackTrace()
//            return
//        }
//
//        // first check the app status
//        // may be in foreground/background/ swiped away
//        /*
//            if in foreground, send board cast to execute noodl, easy case
//            if in background or swiped away,
//                - if  dataPayload == shouldOpen, open activity -> send broadcast
//                - else:  display customized heads up banner
//         */
//        if (isForeground(mPackage)) {
//            val intent = Intent("onNewEcosDoc")
//            intent.putExtras(extras)
//            sendBroadcast(intent)
//        } else {
////            DebugLog.i("Original Priority is " + remoteMessage.getOriginalPriority());
////            DebugLog.i("Priority is " + remoteMessage.getPriority());
//            wakeApp()
//            if (extras.getString("openApp", "").equals("true")) {
//                startActivity(activityClass, extras)
//            } else {
//                showNotification(remoteMessage, dataPayLoad, activityClass, extras)
//            }
//        }
//
//
//        // hard coded if openApp is true then vibrate and ringtone
//        if (extras.getString("openApp", "").equals("true")) {
//            startVibrate()
//            startRingtone()
//        }
//    }
//
//    fun startVibrate() {
//        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(
//                VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
//            )
//        } else {
//            vibrator.vibrate(1000)
//        }
//    }
//
//    fun startRingtone() {
//        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
//        val ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
//        ringtone.play()
//    }
//
//    // [END receive_message]
//    // may be screen is off when receive notification
//    private fun wakeApp() {
//        val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
//        val screenIsOn = pm.isInteractive // check if screen is on
//        if (!screenIsOn) {
//            val wakeLockTag = packageName + "WAKELOCK"
//            val wakeLock = pm.newWakeLock(
//                PowerManager.FULL_WAKE_LOCK or
//                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
//                        PowerManager.ON_AFTER_RELEASE, wakeLockTag
//            )
//
//            //acquire will turn on the display
//            wakeLock.acquire()
//
//            //release will release the lock from CPU, in case of that, screen will go back to sleep mode in defined time bt device settings
//            wakeLock.release()
//        }
//    }
//
//    override fun sendBroadcast(intent:Intent) {
//        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
//    }
//
//    private fun startActivity(activityClass: Class<*>, extras: Bundle) {
//        val myIntent = Intent(this, activityClass)
//        myIntent.flags = (
//                Intent.FLAG_ACTIVITY_NEW_TASK
//                        or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        myIntent.action = "android.intent.action.MAIN"
//        myIntent.addCategory("android.intent.category.LAUNCHER")
//        myIntent.putExtras(extras)
//        this.applicationContext.startActivity(myIntent)
//    }
//
//    private fun createNotificationChannel(channelID: String) {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "Foreground Service Channel"
//            val description = "channel description"
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(channelID, name, importance)
//            channel.description = description
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            val notificationManager = getSystemService(
//                NotificationManager::class.java
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    // https://stackoverflow.com/questions/40181654/firebase-fcm-open-activity-and-pass-data-on-notification-click-android
//    private fun showNotification(
//        remoteMessage: RemoteMessage,
//        dataPayLoad: Map<String, String>,
//        activityClass: Class<*>,
//        extras: Bundle
//    ) {
//        val channelID = "NOTIFICATION_CHANNEL_ID"
//        createNotificationChannel(channelID)
//
//        // Check if message contains a notification payload.
//        val title = if (dataPayLoad.containsKey("title")) dataPayLoad["title"] else "title"
//        val body = if (dataPayLoad.containsKey("body")) dataPayLoad["body"] else "body"
//        val notificationIntent = Intent(applicationContext, activityClass)
//        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//        notificationIntent.action = Intent.ACTION_MAIN
//        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        notificationIntent.putExtras(extras)
//        val resultIntent = PendingIntent.getActivity(
//            applicationContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
//            applicationContext, channelID
//        )
//            .setSmallIcon(R.drawable.sym_def_app_icon)
//            .setContentTitle(title)
//            .setContentText(body)
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(resultIntent)
//        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        mNotificationManager.notify(0, mBuilder.build())
//    }
//    // [START on_new_token]
//    /**
//     * Called if InstanceID token is updated. This may occur if the security of
//     * the previous token had been compromised. Note that this is called when the InstanceID token
//     * is initially generated so this is where you would retrieve the token.
//     */
//
//    override fun onNewToken(token1: String) {
//        token=token1
//        val context = applicationContext
//        val intent = Intent("FCMOnTokenReceive")
//        intent.putExtra("FCMToken", token1)
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//    }
//
//    companion object {
//        // [END on_new_token]
//        /*
//     * Should be called after login (ce 1030 edge),\
//     *  will send the current FCM token to backend
//     */
//
//        var sharedPref: SharedPreferences? = null
//
//        var token: String?
//            get() {
//                return sharedPref?.getString("token", "")
//            }
//            set(value) {
//                sharedPref?.edit()?.putString("token", value)?.apply()
//            }
//
//
//        fun sendCurrentToken(context: Context?) {
//            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener<String?> { task ->
//                if (!task.isSuccessful) {
//                    return@OnCompleteListener
//                }
//                // Get new FCM registration token
//                val token1 = task.result
//                token=token1
//
//                // Log and toast
//                val intent = Intent("FCMOnTokenReceive")
//                intent.putExtra("FCMToken", token1)
//                LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
//            })
//        }
//    }
//}