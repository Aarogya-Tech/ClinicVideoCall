package com.aarogyaforworkers.aarogyaFDC

import android.app.Activity
import android.app.Application
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.util.Rational
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.aarogyaforworkers.aarogyaFDC.composeScreens.showProgress
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayout
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutGalleryConfig
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode
import com.zegocloud.uikit.components.common.ZegoShowFullscreenModeToggleButtonRules
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig
import com.zegocloud.uikit.prebuilt.call.config.ZegoHangUpConfirmDialogInfo
import com.zegocloud.uikit.prebuilt.call.config.ZegoMenuBarButtonName
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallConfigProvider
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import com.zegocloud.uikit.prebuilt.call.invite.internal.IncomingCallButtonListener
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallType
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallUser
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoInvitationCallListener
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Arrays
import java.util.Random

var isfromcall=false
class ZegoCloudViewModel() {
    lateinit var userId: String
    lateinit var username: String
    lateinit var sp: SharedPreferences
    lateinit var callInvitationConfig: ZegoUIKitPrebuiltCallInvitationConfig
    lateinit var application:Application
    lateinit var context: Context
    lateinit var xml:View
    lateinit var navHostController: NavHostController
    var isfromNotification= mutableStateOf(false)


    @Composable
    fun initCallInviteService() {

        val context= LocalContext.current

        val appID: Long = 244180377
        val appSign = "dcf45cdc1d20b50ad9b16b9ac7cd99e34177972dac9ba770b177d8ff9962ae91"
//        val username = UserID + "_" + Build.MANUFACTURER
        callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true
        val notificationConfig = ZegoNotificationConfig()
        notificationConfig.sound = "zegocloudmp3"
        notificationConfig.channelID = "CallInvitation"
        notificationConfig.channelName = "CallInvitation"
        callInvitationConfig.notificationConfig = notificationConfig
        callInvitationConfig.provider = ZegoUIKitPrebuiltCallConfigProvider { invitationData ->

                var config = ZegoUIKitPrebuiltCallConfig.groupVideoCall()

                config.bottomMenuBarConfig.buttons = Arrays.asList(
                    ZegoMenuBarButtonName.TOGGLE_CAMERA_BUTTON,
                    ZegoMenuBarButtonName.SWITCH_CAMERA_BUTTON,
                    ZegoMenuBarButtonName.HANG_UP_BUTTON,
                    ZegoMenuBarButtonName.TOGGLE_MICROPHONE_BUTTON,
                    ZegoMenuBarButtonName.SCREEN_SHARING_TOGGLE_BUTTON,
                    ZegoMenuBarButtonName.BEAUTY_BUTTON,
                    ZegoMenuBarButtonName.SHOW_MEMBER_LIST_BUTTON,
                )

                val galleryConfig = ZegoLayoutGalleryConfig()
                galleryConfig.removeViewWhenAudioVideoUnavailable = true
                galleryConfig.showNewScreenSharingViewInFullscreenMode = true
                galleryConfig.showScreenSharingFullscreenModeToggleButtonRules = ZegoShowFullscreenModeToggleButtonRules.SHOW_WHEN_SCREEN_PRESSED
                config.layout = ZegoLayout(ZegoLayoutMode.GALLERY, galleryConfig)

                config.hangUpConfirmDialogInfo = ZegoHangUpConfirmDialogInfo()
                config.hangUpConfirmDialogInfo.title= "Hangup confirm"
                config.hangUpConfirmDialogInfo.message= "Do you want to hangup?"
                config.hangUpConfirmDialogInfo.cancelButtonName= "Cancel"
                config.hangUpConfirmDialogInfo.confirmButtonName= "Confirm"

                config.durationConfig = ZegoCallDurationConfig()
                config.durationConfig.isVisible = true
                config.durationConfig.durationUpdateListener =
                    DurationUpdateListener { seconds ->
                        if (seconds == (0).toLong()) {
                            CoroutineScope(Dispatchers.Main).launch {

                                ZegoUIKitPrebuiltCallInvitationService.endCall()

                                delay(1000)

//                                isfromcall = true
//                                navHostController.navigate(Destination.VideoCallingLobbyScreen.routes)
                                val intent = Intent(context, VideoConferencing::class.java)
                                context.startActivity(intent)
                            }
                        }
                    }

                config
            }

        ZegoUIKitPrebuiltCallInvitationService.init(
            application, appID, appSign, userId, username,
            callInvitationConfig
        )

        ZegoUIKitPrebuiltCallInvitationService.addIncomingCallButtonListener(object :
            IncomingCallButtonListener {
            override fun onIncomingCallDeclineButtonPressed() {}
            override fun onIncomingCallAcceptButtonPressed() {}
        })
        ZegoUIKitPrebuiltCallInvitationService.addInvitationCallListener(object :
            ZegoInvitationCallListener {
            override fun onIncomingCallReceived(callID: String, caller: ZegoCallUser, callType: ZegoCallType, callees: List<ZegoCallUser>) {}
            override fun onIncomingCallCanceled(callID: String, caller: ZegoCallUser) {}
            override fun onIncomingCallTimeout(callID: String, caller: ZegoCallUser) {}
            override fun onOutgoingCallAccepted(callID: String, callee: ZegoCallUser) {}
            override fun onOutgoingCallRejectedCauseBusy(callID: String?, callee: ZegoCallUser?) {}
            override fun onOutgoingCallDeclined(callID: String, callee: ZegoCallUser) {}
            override fun onOutgoingCallTimeout(callID: String, callees: List<ZegoCallUser>) {}
        })
    }

    fun unInitCallInviteService() {
        ZegoUIKitPrebuiltCallInvitationService.unInit()
    }

    private fun updatedPipParams(): PictureInPictureParams? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
        } else null
    }


//    fun initVideoButton() {
//        val newVideoCall = xml.findViewById<ZegoSendCallInvitationButton>(R.id.new_video_call)
//        newVideoCall.setIsVideoCall(true)
//        //resourceID can be used to specify the ringtone of an offline call invitation,
//        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
//        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
//        newVideoCall.resourceID = "CallInvitation"
//        newVideoCall.setOnClickListener { v: View? ->
//            val inputLayout =
//                xml.findViewById<EditText>(R.id.target_user_id)
//            val targetUserID = inputLayout.text.toString()
//            val split =
//                targetUserID.split(",".toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray()
//            val users: MutableList<ZegoUIKitUser> =
//                ArrayList()
//            for (userID in split) {
//                val userName = userID + "_name"
//                users.add(ZegoUIKitUser(userID, userName))
//            }
//            newVideoCall.setInvitees(users)
//        }
//    }
//
//    fun initVoiceButton() {
//        val newVoiceCall = xml.findViewById<ZegoSendCallInvitationButton>(R.id.new_voice_call)
//        newVoiceCall.setIsVideoCall(false)
//        //resourceID can be used to specify the ringtone of an offline call invitation,
//        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
//        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
//        newVoiceCall.resourceID = "zegouikit_call"
//        newVoiceCall.setOnClickListener { v: View? ->
//            val inputLayout =
//                xml.findViewById<EditText>(R.id.target_user_id)
//            val targetUserID = inputLayout.text.toString()
//            val split =
//                targetUserID.split(",".toRegex()).dropLastWhile { it.isEmpty() }
//                    .toTypedArray()
//            val users: MutableList<ZegoUIKitUser> =
//                ArrayList()
//            for (userID in split) {
//                val userName = userID + "_name"
//                users.add(ZegoUIKitUser(userID, userName))
//            }
//            newVoiceCall.setInvitees(users)
//        }
//        newVoiceCall.setOnClickListener { errorCode, errorMessage, errorInvitees -> }
//    }

    fun initVideoButton() {
        val newVideoCall = xml.findViewById<ZegoSendCallInvitationButton>(R.id.new_video_call)
        newVideoCall.setIsVideoCall(true)
        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        newVideoCall.resourceID = "CallInvitation"
        newVideoCall.setOnClickListener { v: View? ->
//            val inputLayout =
//                xml.findViewById<EditText>(R.id.target_user_id)
//            val targetUserID = inputLayout.text.toString()
            val targetUserID="919340413756"
            val split =
                targetUserID.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val users: MutableList<ZegoUIKitUser> =
                ArrayList()
            for (userID in split) {
                val userName = userID + "_name"
                users.add(ZegoUIKitUser(userID, userName))
            }
            newVideoCall.setInvitees(users)
        }
    }

    fun initVoiceButton() {
        val newVoiceCall = xml.findViewById<ZegoSendCallInvitationButton>(R.id.new_voice_call)
        newVoiceCall.setIsVideoCall(false)
        //resourceID can be used to specify the ringtone of an offline call invitation,
        //which must be set to the same value as the Push Resource ID in ZEGOCLOUD Admin Console.
        //This only takes effect when the notifyWhenAppRunningInBackgroundOrQuit is true.
        newVoiceCall.resourceID = "zegouikit_call"
        newVoiceCall.setOnClickListener { v: View? ->
//            val inputLayout =
//                xml.findViewById<EditText>(R.id.target_user_id)
//            val targetUserID = inputLayout.text.toString()
            val targetUserID="919340413756"
            val split =
                targetUserID.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val users: MutableList<ZegoUIKitUser> =
                ArrayList()
            for (userID in split) {
                val userName = userID + "_name"
                users.add(ZegoUIKitUser(userID, userName))
            }
            newVoiceCall.setInvitees(users)
        }
        newVoiceCall.setOnClickListener { errorCode, errorMessage, errorInvitees -> }
    }

//    fun generateUserID(): String?{
//        val builder = StringBuilder()
//        val random = Random()
//        while (builder.length < 5) {
//            val nextInt = random.nextInt(10)
//            if (builder.length == 0 && nextInt == 0) {
//                continue
//            }
//            builder.append(nextInt)
//        }
//        saveUserId(builder.toString())
//        return builder.toString()
//    }

    fun generateUserID(): String? {
        saveUserId(MainActivity.adminDBRepo.adminProfileState.value.phone)
        return MainActivity.adminDBRepo.adminProfileState.value.phone
    }

    fun generateUserName(): String? {
        val loggedInUser = MainActivity.adminDBRepo.adminProfileState.value.first_name
        saveUserName(loggedInUser)
        return loggedInUser
    }

    fun saveUserId(userID: String) {
        val edit = sp.edit()
        edit.putString("userId", userID)
        edit.apply()
    }

    fun saveUserName(userName:String) {
        val edit = sp.edit()
        edit.putString("usreName",userName)
        edit.apply()
    }

    fun getUserID(): String? {
        val userIdValue = sp.getString("userId", "")
        return if (userIdValue!!.isEmpty()) {
            generateUserID()
        } else {
            userIdValue
        }
    }

    fun getUserName(): String? {
        val userNameValue = sp.getString("userName", "")
        return if (userNameValue!!.isEmpty()) {
            generateUserName()
        } else {
            userNameValue
        }
    }



    companion object {

        @Volatile private var instance: ZegoCloudViewModel? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ZegoCloudViewModel().also { instance = it }
        }
    }
}