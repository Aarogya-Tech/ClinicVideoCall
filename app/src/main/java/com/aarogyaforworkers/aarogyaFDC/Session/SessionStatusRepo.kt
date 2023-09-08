package com.aarogyaforworkers.aarogyaFDC.Session

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class SessionStatusRepo {

    private val status = SessionStatus(false, false, false)

    private val isCurrentSessionStatus = mutableStateOf(status)

    var sessionStatus : State<SessionStatus> = isCurrentSessionStatus

    fun updateSessionState(status: SessionStatus){
        isCurrentSessionStatus.value = status
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: SessionStatusRepo? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SessionStatusRepo().also { instance = it }
            }
    }
}