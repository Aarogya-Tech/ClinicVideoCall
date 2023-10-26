package com.aarogyaforworkers.awsapi

import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile

interface APICallbacks {

    fun onSuccessAdminProfileResult(profile: MutableList<AdminProfile>)

    fun onSuccessAdminsGroupProfiles(profile: MutableList<AdminProfile>)

    fun onSuccessAdminProfilePicUpdated(newPicURL : String)

    fun onAdminProfilePicUpdateFailed()

    fun onSubUserProfileFound(profile: MutableList<SubUserProfile>)

    fun onSubUserProfileNotFound()

    fun onSuccessAdminProfileTokenUpdated()

    fun onAdminProfileTokenUpdateFailed()

    fun onGuestSessionsDeleted()

    fun onGuestSessionDeleteFailed()

    fun userAllReadyRegistered()

    fun userIsNotRegistered()

    fun onSearchSubUserProfileResult(profile: MutableList<SubUserProfile>)

    fun onSuccessSubUserSessions(sessions: MutableList<Session>)

    fun onNoEmailFoundByPhone(error : String)

    fun onSuccessfullyEmailFoundByPhone(email : String)

    fun onSuccessfullyEmailFoundByPhone(email : String, password : String)

    fun onFailedSubUserSessions()

    fun onSingleSessionDeleted()

    fun onSingleSessionUpdated()

    fun onSessionDeleteFailed()

    fun onSuccessRemarkUpdate()

    fun onSuccessSessionUpdate()

    fun onFailedSessionUpdate()

    fun onSuccessSessionDeleted()

    fun onFailedSessionDelete()

    fun onFailedRemarkUpdate()

    fun onSuccessPostSession()

    fun onFailedPostSession()

    fun onCreateUpdateSubUserProfileResult(isSuccess: Boolean)

    fun onFailedAdminGroupProfileResult()

    fun onFailedAdminProfileResult(withError : String)

    fun onFailedSearchProfileResult()

    fun onSuccessSubUserVerificationCodeSent(verificationCode : String)

    fun onVerificationCodeFailed()

    fun onSuccessGetTotalRegistrationCounts(counts : Int)

    fun onFailedToGetRegistrationCount()

    fun onSuccessRegistrationCountUpdated()

    fun onFailedToUpdateRegistrationCount()

}