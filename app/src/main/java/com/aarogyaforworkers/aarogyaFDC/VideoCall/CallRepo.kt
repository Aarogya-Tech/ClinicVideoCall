package com.aarogyaforworkers.aarogyaFDC.VideoCall

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogyaFDC.Data
import com.aarogyaforworkers.aarogyaFDC.PushNotification
import com.aarogyaforworkers.aarogyaFDC.composeScreens.fetchImageFromUrl
import com.aarogyaforworkers.awsapi.models.AdminProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CallRepo {

    private var isConfrenceId : MutableState<String?> = mutableStateOf(null)

    var confrenceId : State<String?> = isConfrenceId

    fun updateConfrenceId(id : String?){
        isConfrenceId.value = id
    }

    var isOnCallScreen = false

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
            isprofileBitmap.value = getCroppedBitmap(fetchImageFromUrl(receiverProfileUrl.value!!))
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


    companion object {

        // Singleton instantiation you already know and love
        @Volatile private var instance: CallRepo? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CallRepo().also { instance = it }
            }
    }
}