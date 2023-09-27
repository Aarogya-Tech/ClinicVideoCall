package com.aarogyaforworkers.aarogyaFDC.Auth
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.ImageWithCaptions
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.Pdf
import com.aarogyaforworkers.aarogyaFDC.composeScreens.isFromLRSave
import com.aarogyaforworkers.aarogyaFDC.composeScreens.isLRSetUpDone
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

    override fun onSuccessPatientDocUploaded(withUrl: String, name : String) {
        val pdf = Pdf(name, withUrl)
        if(isFromVital){
            MainActivity.sessionRepo.updatePdfList(pdf)
            MainActivity.adminDBRepo.updatePDfUploadState(true)
        }else{
            var selectedSession_ = MainActivity.sessionRepo.selectedsession
            val parsedText = selectedSession_!!.LabotryRadiology.split("-:-")
            if(parsedText.size == 3){
                val text = parsedText.first()
                MainActivity.sessionRepo.clearImageList()
                MainActivity.sessionRepo.clearPdfList()
                val listIOfImages = MainActivity.sessionRepo.parseImageList(parsedText[1])
                if(listIOfImages.isEmpty()){
                    MainActivity.sessionRepo.clearImageList()
                }else{
                    listIOfImages.forEach {
                        MainActivity.sessionRepo.updateImageWithCaptionList(it)
                    }
                }
                val listOfPdf = MainActivity.sessionRepo.parsePdfList(parsedText[2])
                if(listOfPdf.isEmpty()){
                    MainActivity.sessionRepo.clearPdfList()
                }else{
                    listOfPdf.forEach {
                        MainActivity.sessionRepo.updatePdfList(it)
                    }
                }
                MainActivity.sessionRepo.updatePdfList(pdf)
                val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                val newUpdatedPdfList = MainActivity.sessionRepo.pdfList.value.filterNotNull().toString()
                if(selectedSession_ != null){
                    selectedSession_.LabotryRadiology = "${text}-:-${newUpdatedList}-:-${newUpdatedPdfList}"
                    if(!isFromVital){
                        MainActivity.sessionRepo.updateSession(selectedSession_)
                    }else{
                        MainActivity.adminDBRepo.updatePDfUploadState(true)
                    }
                }
            }
        }

    }

    override fun onDocUploadFailed() {
        MainActivity.adminDBRepo.updatePDfUploadState(false)
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