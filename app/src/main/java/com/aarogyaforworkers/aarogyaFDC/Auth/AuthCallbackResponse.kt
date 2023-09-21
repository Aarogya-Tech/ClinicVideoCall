package com.aarogyaforworkers.aarogyaFDC.Auth
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.ImageWithCaptions
import com.aarogyaforworkers.awsauth.AuthCallbacks
import java.io.File

class AuthCallbackResponse : AuthCallbacks {

    override fun onSignInSuccess() {
        MainActivity.authRepo.updateSignInState(true)
    }

    override fun onSignInOTPSent() {
        MainActivity.authRepo.updateSignInOTPState(true)
    }

    override fun onSignInOTPFailed() {
        MainActivity.authRepo.updateSignInOTPState(false)
    }

    override fun onAdminUserIdUpdate(id: String) {
        MainActivity.authRepo.updateAdminUID(id)
    }

    override fun onSignOutSuccess() {
        MainActivity.authRepo.updateSignOutState(true)
    }

    override fun onSignInFailed(reason: String) {
        MainActivity.authRepo.updateSignInState(false)
    }

    override fun onForgotPasswordConfirmationOTPSent() {
        MainActivity.authRepo.updateForgotPasswordOTPState(true)
    }

    override fun onForgorPasswordUserNotFound() {
        MainActivity.authRepo.updateEmailNotFound(true)
    }

    override fun onForgotPasswordConfirmationOTPFailed(reason: String) {
        MainActivity.authRepo.updateForgotPasswordOTPState(false)
    }

    override fun onSuccessFullPasswordReset() {
        MainActivity.authRepo.updatePasswordResetState(true)
    }

    override fun onPasswordResetFailure(reason: String) {
        MainActivity.authRepo.updatePasswordResetState(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEcgFileUploaded(withLink: String) {
        // update session in cloud ->
        if(MainActivity.sessionRepo.selectedsession != null){
            MainActivity.sessionRepo.selectedsession!!.ecgFileLink = withLink
            MainActivity.sessionRepo.createNewSession(MainActivity.sessionRepo.selectedsession!!)
        }
        MainActivity.subUserRepo.updateSessionInCloud(withLink, MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo)
        if(!MainActivity.pc300Repo.isOnSessionPage){
            MainActivity.pc300Repo.addEcgSession(withLink)
        }
    }

    override fun onEcgFileUploadedFailed(withFile: File) {
        MainActivity.s3Repo.startUploadingFile(withFile)
    }

    override fun onSuccessFullySubUserprofileUpdate(withImageUrl: String) {
        MainActivity.adminDBRepo.createOrUpdateSubUserWithProfilePic(withImageUrl)
    }

    override fun onFailedToUploadSessionAttachment() {
        MainActivity.sessionRepo.updateAttachmentUploadedStatus(false)
    }

    override fun onSuccessFullyAdminProfileUploaded(withImageUrl: String) {
        val user = MainActivity.adminDBRepo.getLoggedInUser()
        user.profile_pic_url = withImageUrl
        user.first_name = user.first_name.replace("Dr.", "").replace(" ", "")
        MainActivity.adminDBRepo.updateAdminProfilePic(user)
    }

    override fun onSuccessFullySessionSummaryUploaded(withImageUrl: String) {
        MainActivity.s3Repo.updateSessionSummaryUploadStatus(true, withImageUrl)
    }

    override fun onSuccessSessionAttachmentUploaded(caption: String, withImageUrl: String, type : Int) {
        MainActivity.cameraRepo.updateDownloadedImage(withImageUrl, MainActivity.cameraRepo.capturedImageBitmap.value)
        MainActivity.cameraRepo.updateCapturedImage(null)
        MainActivity.sessionRepo.updateImageWithCaptionList(ImageWithCaptions(caption, withImageUrl))
        MainActivity.sessionRepo.updateAttachmentUploadedStatus(true)
    }

    override fun onSessionSummaryUploadFailed() {
        MainActivity.s3Repo.updateSessionSummaryUploadStatus(false, "")
        MainActivity.sessionRepo.updateAttachmentUploadedStatus(false)
    }

    override fun onFailedAdminProfileUpload() {
        Log.d("TAG", "onFailedAdminProfileUpload: ")
    }

    override fun onSubUserImageUploadFailed() {
        Log.d("TAG", "onSubUserImageUploadFailed: ")
    }

    override fun onInvalidPassword() {
        MainActivity.authRepo.updateWrongPassword(true)
    }

    override fun onInvalidOTP() {
        MainActivity.authRepo.updateWrongOTP(true)
    }

    override fun onInvalidUserName() {
        MainActivity.authRepo.updateWrongUserName(true)
    }

    override fun onUserAllReadySignedIn() {
        MainActivity.authRepo.updateIsAllReadyLoggedIn(true)
    }

    override fun onWrongPasswordResetOTP() {
        MainActivity.authRepo.updateWrongOTP(true)
    }

}