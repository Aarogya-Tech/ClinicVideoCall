package com.aarogyaforworkers.aarogyaFDC.VideoCall

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.awsapi.models.AdminProfile
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

    companion object {

        // Singleton instantiation you already know and love
        @Volatile private var instance: CallRepo? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CallRepo().also { instance = it }
            }
    }
}