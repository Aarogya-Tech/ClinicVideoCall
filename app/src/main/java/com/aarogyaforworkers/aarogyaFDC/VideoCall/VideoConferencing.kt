package com.aarogyaforworkers.aarogyaFDC.VideoCall

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.R
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
        if(intent.action=="ACTION_ACCEPT"){
            FirebaseMessagingService.notificationManager.cancel(FirebaseMessagingService.notificationID!!)
        }
        addFragment()
    }
    private fun addFragment() {

        val pLocal =  ProfilePreferenceManager.getInstance(this)

        val appID: Long = 582070918

        val appSign = "5b7ca60cc23f8aed21f37e0682593bdf3b5aae9bebe27eb3f7ca83ad985ca62a"

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

        fragment.setLeaveVideoConferenceListener {
            MainActivity.callRepo.isOnCallScreen = false
//            MainActivity.callRepo.updateGroupMembersProfileList(arrayListOf())
            finishAndRemoveTask()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow();

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
            finish()
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
    }
}