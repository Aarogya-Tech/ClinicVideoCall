package com.aarogyaforworkers.aarogyaFDC.VideoCall

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.os.Handler
import android.system.Os.link
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.aarogyaforworkers.aarogyaFDC.Constants
import com.aarogyaforworkers.aarogyaFDC.Data
import com.aarogyaforworkers.aarogyaFDC.PushNotification
import com.aarogyaforworkers.aarogyaFDC.R
import com.aarogyaforworkers.aarogyaFDC.VideoConferencing
import com.aarogyaforworkers.aarogyaFDC.composeScreens.fetchImageFromUrl
import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CallRepo {

    var VideoConferenceContext : Activity? = null

    private var isConfrenceId : MutableState<String?> = mutableStateOf(null)

    var confrenceId : State<String?> = isConfrenceId

    fun updateConfrenceId(id : String?){
        isConfrenceId.value = id
    }

    private var isNoMissedCall : MutableState<Boolean?> = mutableStateOf(false)

    var NoMissedCall : State<Boolean?> = isNoMissedCall

    fun updateNoMissedCall(bool:Boolean){
        isNoMissedCall.value = bool
    }

    var isOnCallScreen = false

    var isCallAccepted = false

    var isCallee=false

    private var callerProfile = AdminProfile("","","","","","","","","","","","","", "","","","","","","", "")

    private var isSelectedCallersProfile = mutableStateOf(mutableListOf(callerProfile))

    var selectedCallersProfile : State<MutableList<AdminProfile>> = isSelectedCallersProfile
    fun updateGroupMembersProfileList(profileList : MutableList<AdminProfile>){
        isSelectedCallersProfile.value = arrayListOf()
        isSelectedCallersProfile.value = profileList
    }

    private var isReceiverName : MutableState<String?> = mutableStateOf(null)

    var receiverName : State<String?> = isReceiverName

    fun updateReceiverName(name : String?){
        isReceiverName.value = name
    }

    private var isReceiverClinicName : MutableState<String?> = mutableStateOf(null)

    var receiverClinicName : State<String?> = isReceiverClinicName

    fun updateReceiverClinicName(name : String?){
        isReceiverClinicName.value = name
    }

    private var isReceiverProfileUrl : MutableState<String?> = mutableStateOf(null)

    var receiverProfileUrl : State<String?> = isReceiverProfileUrl

    fun updateReceiverProfileUrl(url : String?){
        isReceiverProfileUrl.value = url
    }

    private var isprofileBitmap : MutableState<Bitmap?> = mutableStateOf(null)

    var profileBitmap : State<Bitmap?> = isprofileBitmap

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }

    fun updateProfileBitmap(){
        if(receiverProfileUrl.value==null)
        {
            Log.i("","")
        }
        else
        {
            try {
                var bitmap=fetchImageFromUrl(receiverProfileUrl.value!!)
                isprofileBitmap.value = getCroppedBitmap(bitmap)
            }
            catch (e:Exception)
            {
                isprofileBitmap.value = null
                Log.i("TAG","Image Download Error")
            }
        }
    }

    private var isReceiverToken : MutableState<String?> = mutableStateOf(null)

    var receiverToken : State<String?> = isReceiverToken

    fun updateReceiverToken(token : String?){
        isReceiverToken.value = token
    }

    fun refreshConfrenceId(){
        val id = UUID.randomUUID().toString()
        FirebaseMessagingService.confrenceId = id
        updateConfrenceId(id)
    }

    fun sendCancelCallNotification(token : String){
        PushNotification(
            token,
            Data("End Call")
        ).also {
            sendNotification(it)
        }
    }

    fun sendMissedCallNotificationToCallee(token : String){
        PushNotification(
            token,
            Data("End Call")
        ).also {
            sendNotification(it)
        }
    }

    fun sendAcceptNotificationToCaller(token : String){
        PushNotification(
            token,
            Data("Accept Call")
        ).also {
            sendNotification(it)
        }
    }

    fun sendCancelCallNotificationToCaller(token : String){
        PushNotification(
            token,
            Data("End Call Callee")
        ).also {
            sendNotification(it)
        }
    }


    fun sendCancelCallNotificationMultiple(token : String){
        PushNotification(
            token,
            Data("End Calls")
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

    val timer = object : CountDownTimer(120000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = millisUntilFinished / 1000
            if(!VideoConferencing.callRepo.isOnCallScreen)
            {
                if(VideoConferencing.callRepo.selectedCallersProfile.value.size > 1){
                    VideoConferencing.callRepo.selectedCallersProfile.value.forEach {
                        VideoConferencing.callRepo.sendCancelCallNotificationMultiple(it.token)
                    }
                }
                VideoConferencing.mediaPlayer!!.stop()
                cancel()
            }
        }

        override fun onFinish() {
            VideoConferencing.mediaPlayer!!.stop()
            if(VideoConferencing.callRepo.selectedCallersProfile.value.size == 1 && VideoConferencing.callRepo.isOnCallScreen && !VideoConferencing.callRepo.isCallAccepted){
                VideoConferencing.callRepo.isOnCallScreen = false
                VideoConferencing.callRepo.sendMissedCallNotificationToCallee(VideoConferencing.callRepo.selectedCallersProfile.value.first().token)
                VideoConferencing.callRepo.VideoConferenceContext!!.finishAndRemoveTask()
            }
            VideoConferencing.callRepo.isCallAccepted=false
        }
    }

    val timerCallee = object : CountDownTimer(120000, 1000) {
        override fun onTick(p0: Long) {
        }

        override fun onFinish() {
            if(!isOnCallScreen && NoMissedCall.value==false)
            {
                FirebaseMessagingService.notificationManager.cancel(FirebaseMessagingService.notificationID!!)

                val notification = NotificationCompat.Builder(FirebaseMessagingService.context, Constants.CHANNEL_ID_MissedCall)
                    .setContentTitle(FirebaseMessagingService.callRepo.receiverClinicName.value)
                    .setContentText("Missed Group Call")
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
                FirebaseMessagingService.notificationManagerMissed.notify(FirebaseMessagingService.notificationIDMissed!!, notification)
            }
            else
            {
                FirebaseMessagingService.callRepo.updateNoMissedCall(false)
            }
        }
    }


    companion object {

        // Singleton instantiation you already know and love
        @Volatile private var instance: CallRepo? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CallRepo().also { instance = it }
            }
    }
}