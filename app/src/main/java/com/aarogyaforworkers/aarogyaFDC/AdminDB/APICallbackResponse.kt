package com.aarogyaforworkers.aarogyaFDC.AdminDB

import android.util.Log
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.awsapi.APICallbacks
import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile

class APICallbackResponse : APICallbacks{

    override fun onSuccessAdminProfileResult(profile: MutableList<AdminProfile>) {
        if(profile.size != 0){
            MainActivity.adminDBRepo.updateAdminProfile(profile[0])
            MainActivity.adminDBRepo.updateAdminProfileSyncedState(true)
        }
    }

    override fun onSuccessAdminProfilePicUpdated(newPicURL: String) {
        MainActivity.adminDBRepo.updateAdminProfilePicUpdateStatus(newPicURL)
        MainActivity.adminDBRepo.updateAdminProfileUpdateState(true)
    }

    override fun onAdminProfilePicUpdateFailed() {
        MainActivity.adminDBRepo.updateAdminProfileUpdateState(false)
        MainActivity.adminDBRepo.updateAdminProfilePicUpdateStatus("")
    }

    override fun onSubUserProfileFound(profile: MutableList<SubUserProfile>) {
        //
    }

    override fun onSubUserProfileNotFound() {
        //
    }

    override fun onGuestSessionsDeleted() {
        MainActivity.adminDBRepo.updateIsGuestSessionDeleted(true)
    }

    override fun onGuestSessionDeleteFailed() {
        MainActivity.adminDBRepo.updateIsGuestSessionDeleted(false)
    }

    override fun userAllReadyRegistered() {
        MainActivity.subUserRepo.updateCurrentPhoneRegistrationState(true)
        MainActivity.subUserRepo.isUserAllReadyPresent.value = true
        MainActivity.adminDBRepo.updateUserRegisteredState(true)
    }

    override fun userIsNotRegistered() {
        MainActivity.subUserRepo.updateCurrentPhoneRegistrationState(false)
        MainActivity.subUserRepo.isUserAllReadyPresent.value = false
        MainActivity.adminDBRepo.updateUserNotRegisteredState(true)
    }

    override fun onSearchSubUserProfileResult(profile: MutableList<SubUserProfile>) {
        MainActivity.adminDBRepo.updateSearchedState(true)
        MainActivity.adminDBRepo.updateSearchUserList(profile)
    }

    override fun onSuccessSubUserSessions(sessions: MutableList<Session>) {
        MainActivity.sessionRepo.updateSessionFetchStatus(true)
        MainActivity.subUserRepo.updateSessionsResponseList(sessions)
        Log.d("TAG", "updateSessionsResponseList: created ")
    }

    override fun onNoEmailFoundByPhone(error: String) {
        MainActivity.authRepo.updateEmailNotFound(true)
    }

    override fun onSuccessfullyEmailFoundByPhone(email: String) {
        MainActivity.authRepo.getSignInEmailOTP(email)
    }

    override fun onSuccessfullyEmailFoundByPhone(email: String, password: String) {
        MainActivity.authRepo.signInWithEmailPassword(email, password)
    }

    override fun onFailedSubUserSessions() {
        MainActivity.sessionRepo.updateSessionFetchStatus(false)
        Log.d("tag", "onFailedSubUserSessions: ")
    }

    override fun onSingleSessionDeleted() {
        // ResetSessionState
        MainActivity.subUserRepo.updateSessionResetState()
        MainActivity.adminDBRepo.updateIsGuestSessionDeleted(true)
    }


    override fun onSingleSessionUpdated() {
        // Keep session state to update untill its not reset or restart
        MainActivity.subUserRepo.updateSessionUpdateState()
    }

    override fun onSessionDeleteFailed() {
        MainActivity.subUserRepo.updateSessionFailedToSave()
        MainActivity.adminDBRepo.updateIsGuestSessionDeleted(false)
    }

    override fun onSuccessRemarkUpdate() {
        MainActivity.subUserRepo.updateSessionUpdated(true)
    }

    override fun onSuccessSessionUpdate() {
        MainActivity.sessionRepo.updateIsSessionUpdatedStatus(true)
    }

    override fun onFailedSessionUpdate() {
        MainActivity.sessionRepo.updateIsSessionUpdatedStatus(false)
    }

    override fun onSuccessSessionDeleted() {
        MainActivity.sessionRepo.updateSessionDeletedStatus(true)
    }

    override fun onFailedSessionDelete() {
        MainActivity.sessionRepo.updateSessionDeletedStatus(false)
    }

    override fun onFailedRemarkUpdate() {
        MainActivity.subUserRepo.updateSessionUpdated(false)
    }

    override fun onSuccessPostSession() {
        Log.d("TAG", "onSuccessPostSession: ")

        MainActivity.sessionRepo.updateIsSessionCreatedStatus(true)
//        MainActivity.sessionRepo.updateNewSessionCreate(false)


        // Make session in update state it is created so user can now update it
        when(MainActivity.subUserRepo.isForSaveAndRestartAction){
            true -> {
                MainActivity.subUserRepo.updateSessionRestartState()
            }
            false -> {
                if(!MainActivity.subUserRepo.isCurrentSessionSaved){
                    MainActivity.subUserRepo.isCurrentSessionSaved = true
                    // session is saved update its state
                    MainActivity.subUserRepo.updateSessionSaveState()
                } else {
                    // session is updated update its state
                    MainActivity.subUserRepo.updateSessionUpdateState()
                    MainActivity.subUserRepo.isCurrentSessionUpdated = true
                }
            }
        }
        MainActivity.subUserRepo.updateIsUploading(false)
//        MainActivity.subUserRepo.updateIsBufferThere(false)
    }

    override fun onFailedPostSession() {
        Log.d("TAG", "onFailedPostSession: ")
//        MainActivity.sessionRepo.updateNewSessionCreate(false)
        MainActivity.sessionRepo.updateIsSessionCreatedStatus(false)
        MainActivity.subUserRepo.updateSessionFailedToSave()
        MainActivity.subUserRepo.updateIsUploading(false)
    }

    override fun onCreateUpdateSubUserProfileResult(isSuccess: Boolean) {
        Log.d("TAG", "AddNewUserScreen: procressAlert session saving cloud true = ${isSuccess} ")
        MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(isSuccess)
    }

    override fun onFailedAdminProfileResult(withError: String) {
        MainActivity.adminDBRepo.updateAdminProfileNotFound(true)
        MainActivity.adminDBRepo.updateAdminProfileSyncedState(false)

    }

    override fun onFailedSearchProfileResult() {
        MainActivity.adminDBRepo.updateSearchedState(false)
        MainActivity.adminDBRepo.updateSubUserProfileNotFound(true)
    }

    override fun onSuccessSubUserVerificationCodeSent(verificationCode: String) {
        MainActivity.adminDBRepo.setLastVerificationOTP(verificationCode)
    }

    override fun onVerificationCodeFailed() {
        MainActivity.adminDBRepo.setLastVerificationOTP("")
    }

    override fun onSuccessGetTotalRegistrationCounts(counts: Int) {
        MainActivity.adminDBRepo.updateTotalRegistrationCount(counts)
        MainActivity.adminDBRepo.updateRegistrationCountSyncedState(true)
    }

    override fun onFailedToGetRegistrationCount() {
        MainActivity.adminDBRepo.updateRegistrationCountSyncedState(false)
    }

    override fun onSuccessRegistrationCountUpdated() {
        MainActivity.adminDBRepo.updateRegistrationCountUpdatedState(true)
    }

    override fun onFailedToUpdateRegistrationCount() {
        MainActivity.adminDBRepo.updateRegistrationCountUpdatedState(false)
    }
}