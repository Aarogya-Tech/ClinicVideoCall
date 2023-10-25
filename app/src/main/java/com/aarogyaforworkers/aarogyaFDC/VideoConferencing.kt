package com.aarogyaforworkers.aarogyaFDC

import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayout
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutGalleryConfig
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode
import com.zegocloud.uikit.components.common.ZegoShowFullscreenModeToggleButtonRules
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment.LeaveCallListener
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoMenuBarButtonName
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment
import com.zegocloud.uikit.prebuilt.videoconference.config.ZegoPrebuiltVideoConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Arrays
import java.util.Random


class VideoConferencing : AppCompatActivity() {

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



        addFragment()

    }
    fun addFragment() {

        val userId=generateUserID()!!

        val appID: Long = 582070918
        val appSign: String = "5b7ca60cc23f8aed21f37e0682593bdf3b5aae9bebe27eb3f7ca83ad985ca62a"

        val conferenceID = "test_conference_id"
//        val userName = generateUserID + "_" + Build.MANUFACTURER

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

//        MainActivity.zegoCloudViewModel.sp = getSharedPreferences("offline", Context.MODE_PRIVATE)
//        MainActivity.zegoCloudViewModel.userId= MainActivity.zegoCloudViewModel.getUserID()!!
//        MainActivity.zegoCloudViewModel.username= MainActivity.zegoCloudViewModel.getUserName()!!



        val fragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(
            appID, appSign, userId, "gaonaiew", conferenceID, config
        )

        fragment.setLeaveVideoConferenceListener {
            finish()
        }


        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow();

    }

    fun generateUserID(): String?{
        val builder = StringBuilder()
        val random = Random()
        while (builder.length < 5) {
            val nextInt = random.nextInt(10)
            if (builder.length == 0 && nextInt == 0) {
                continue
            }
            builder.append(nextInt)
        }
        return builder.toString()
    }

//    override fun onUserLeaveHint() {
//        super.onUserLeaveHint()
//        if(!isPipSupported) {
//            finish()
//            return
//        }
//        updatedPipParams()?.let { params ->
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                enterPictureInPictureMode(params)
//            }
//        }
//    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onUserLeaveHint()
        if(!isPipSupported) {
            finish()
            return
        }
        updatedPipParams()?.let { params ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPictureInPictureMode(params)
            }
        }

        val context=this

        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)

            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
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