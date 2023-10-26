package com.aarogyaforworkers.aarogyaFDC.VideoCall

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogyaFDC.FirebaseRepo.FirebaseRepo
import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallType
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallUser
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoInvitationCallListener
import java.util.UUID

class CallRepo {

    private var isConfrenceId : MutableState<String?> = mutableStateOf(null)

    var confrenceId : State<String?> = isConfrenceId

    fun updateConfrenceId(id : String?){
        isConfrenceId.value = id
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

    fun refreshConfrenceId(){
        updateConfrenceId(UUID.randomUUID().toString())
    }

    var selectedProfile : AdminProfile? = null

    fun listenCall(){


        ZegoUIKitPrebuiltCallInvitationService.addInvitationCallListener(object :
            ZegoInvitationCallListener {
            override fun onIncomingCallReceived(callID: String, caller: ZegoCallUser, callType: ZegoCallType, callees: List<ZegoCallUser>) {
                Log.d("TAG", "ZEGO: onIncomingCallReceived: ")
            }
            override fun onIncomingCallCanceled(callID: String, caller: ZegoCallUser) {
                Log.d("TAG", "ZEGO: onIncomingCallCanceled: ")
            }
            override fun onIncomingCallTimeout(callID: String, caller: ZegoCallUser) {
                Log.d("TAG", "ZEGO: onIncomingCallTimeout: ")
            }
            override fun onOutgoingCallAccepted(callID: String, callee: ZegoCallUser) {
                Log.d("TAG", "ZEGO: onOutgoingCallAccepted: ")
            }
            override fun onOutgoingCallRejectedCauseBusy(callID: String?, callee: ZegoCallUser?) {
                Log.d("TAG", "ZEGO: onOutgoingCallRejectedCauseBusy: ")
            }
            override fun onOutgoingCallDeclined(callID: String, callee: ZegoCallUser) {
                Log.d("TAG", "ZEGO: onOutgoingCallDeclined: ")
            }
            override fun onOutgoingCallTimeout(callID: String, callees: List<ZegoCallUser>) {
                Log.d("TAG", "ZEGO: onOutgoingCallTimeout: ")
            }
        })
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