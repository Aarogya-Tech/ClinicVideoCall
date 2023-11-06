package com.aarogyaforworkers.aarogyaFDC

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.R
import com.aarogyaforworkers.aarogyaFDC.VideoCall.CallRepo
import com.aarogyaforworkers.aarogyaFDC.VideoCall.FirebaseMessagingService
import com.aarogyaforworkers.aarogyaFDC.VideoConferencing.Companion.callRepo
import com.aarogyaforworkers.aarogyaFDC.storage.ProfilePreferenceManager
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayout
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutGalleryConfig
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode
import com.zegocloud.uikit.components.common.ZegoShowFullscreenModeToggleButtonRules
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMenuBarButtonName
import java.util.Arrays
import java.util.Random


class VideoConferencing : AppCompatActivity() {

    companion object{
        val callRepo = CallRepo.getInstance()
        var mediaPlayer:MediaPlayer?=null
    }

    private val isPipSupported by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageManager.hasSystemFeature(
                PackageManager.FEATURE_PICTURE_IN_PICTURE
            )
        } else {
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_conferencing)

        mediaPlayer = MediaPlayer.create(this, R.raw.outgoingringtonemusic)
        mediaPlayer!!.isLooping=true

        callRepo.VideoConferenceContext=this
        if(intent.action=="ACTION_ACCEPT"){
            callRepo.isCallee=true
            callRepo.isOnCallScreen = true
            FirebaseMessagingService.cancelNotification()
            if(callRepo.receiverToken.value != null){
                if(callRepo.receiverToken.value!!.isNotEmpty()){
                    Log.d("TAG", "onReceive: notification Accept ${callRepo.receiverToken.value}")
                    callRepo.sendAcceptNotificationToCaller(callRepo.receiverToken.value!!)
                }
                else{
                    FirebaseMessagingService.callRepo.updateNoMissedCall(true)
                }
            }
            else{
                FirebaseMessagingService.callRepo.updateNoMissedCall(true)
            }
        }
        else
        {
            callRepo.timer.start()
            if(MainActivity.callRepo.selectedCallersProfile.value.size == 1) {
                mediaPlayer!!.start()
            }
        }
        addFragment()
    }
    fun addFragment() {

        val pLocal =  ProfilePreferenceManager.getInstance(this)

        val appID: Long = 1021361643

        val appSign = "710bf26caac2f836b659c5661fb673c9f31c6b4121337c5ad7b76df3d36c3039"

        val conferenceID = callRepo.confrenceId.value!!

        val config = ZegoUIKitPrebuiltVideoConferenceConfig()

        config.bottomMenuBarConfig.buttons = Arrays.asList(
            ZegoMenuBarButtonName.TOGGLE_CAMERA_BUTTON,
            ZegoMenuBarButtonName.TOGGLE_MICROPHONE_BUTTON,
            ZegoMenuBarButtonName.LEAVE_BUTTON,
            ZegoMenuBarButtonName.SWITCH_AUDIO_OUTPUT_BUTTON,
            ZegoMenuBarButtonName.SCREEN_SHARING_TOGGLE_BUTTON,
            ZegoMenuBarButtonName.CHAT_BUTTON,
            ZegoMenuBarButtonName.SWITCH_CAMERA_BUTTON,
            ZegoMenuBarButtonName.SHOW_MEMBER_LIST_BUTTON
        )

        val galleryConfig = ZegoLayoutGalleryConfig()
        galleryConfig.removeViewWhenAudioVideoUnavailable = true
        galleryConfig.showNewScreenSharingViewInFullscreenMode = true
        galleryConfig.showScreenSharingFullscreenModeToggleButtonRules = ZegoShowFullscreenModeToggleButtonRules.SHOW_WHEN_SCREEN_PRESSED
        config.layout= ZegoLayout(ZegoLayoutMode.GALLERY,galleryConfig)
        val fragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(
            appID, appSign, pLocal.getAdminId(), pLocal.getCallerName(), conferenceID, config
        )
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow();

        fragment.setLeaveVideoConferenceListener {
            supportFragmentManager.beginTransaction().remove(fragment).commit();

            if(callRepo.receiverToken.value != null){
                if(callRepo.receiverToken.value!!.isNotEmpty()){
                    callRepo.sendCancelCallNotification(callRepo.receiverToken.value!!)
                }
            }

            if(!callRepo.isCallee)
            {
                callRepo.isCallAccepted=false
                if(callRepo.selectedCallersProfile.value.size == 1){
                    callRepo.sendCancelCallNotification(callRepo.selectedCallersProfile.value.first().token)
                }
            }


            callRepo.isCallee=false
            callRepo.isOnCallScreen = false
            finishAndRemoveTask()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        startActivity(intent)

        if (isPipSupported) {
            val params = PictureInPictureParams.Builder().build()
            enterPictureInPictureMode(params)
        } else {
            super.onBackPressed()
        }
    }

    private fun updatedPipParams(): PictureInPictureParams? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
        } else null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        if (lifecycle.currentState == Lifecycle.State.CREATED) {
            finishAndRemoveTask()
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
    }

}