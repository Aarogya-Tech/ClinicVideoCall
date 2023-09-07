package com.aarogyaforworkers.aarogyaFDC.PatientSession

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.ImageWithCaptions
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.Options
import com.aarogyaforworkers.awsapi.APIManager
import com.aarogyaforworkers.awsapi.models.Session
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class PatientSessionManagerRepo {

    var selectedsession : Session? = null

    private val imageWithCaptions : ImageWithCaptions? = null

    private var isimageWithCaptionsList = mutableStateOf(mutableListOf(imageWithCaptions))

    var imageWithCaptionsList : MutableState<MutableList<ImageWithCaptions?>> = isimageWithCaptionsList

    private var isAttachmentUploaded : MutableState<Boolean?> = mutableStateOf(null)

    var attachmentUploadedStatus : State<Boolean?> = isAttachmentUploaded

    fun updateAttachmentUploadedStatus(isUploaded : Boolean?){
        isAttachmentUploaded.value = isUploaded
    }
    fun updateImageWithCaptionList(imageWithCaptions: ImageWithCaptions){
        if(!isimageWithCaptionsList.value.contains(imageWithCaptions)){
            isimageWithCaptionsList.value.add(imageWithCaptions)
        }
    }

    fun clearImageList(){
        isimageWithCaptionsList.value = arrayListOf()
    }

    private var isSessionCreated : MutableState<Boolean?> = mutableStateOf(null)

    var sessionCreatedStatus : State<Boolean?> = isSessionCreated

    fun updateIsSessionCreatedStatus(isCreated : Boolean?){
        isSessionCreated.value = isCreated
    }

    private var isSessionUpdated : MutableState<Boolean?> = mutableStateOf(null)

    var sessionUpdatedStatus : State<Boolean?> = isSessionUpdated

    fun updateIsSessionUpdatedStatus(isUpdated : Boolean?){
        isSessionUpdated.value = isUpdated
    }

    private fun createNewSession(session: Session) = APIManager.shared.createPatientSession(session)

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewEmptySessionForUser(userId : String){
        val currentDateTime = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val currentDate = currentDateTime.format(dateFormatter)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val currentTime = currentDateTime.format(timeFormatter)
        val sessionId = userId.take(6)+UUID.randomUUID().toString().takeLast(6)
        val location = MainActivity.locationRepo.userLocation.value
        val emptySession = Session(
            date = currentDate,
            time = currentTime,
            deviceId = "",
            userId = userId,
            adminId = MainActivity.adminDBRepo.getLoggedInUser().admin_id,
            sessionId = sessionId,
            sys = "",
            dia = "",
            heartRate = "",
            spO2 = "",
            weight = "",
            bodyFat = "",
            temp = "",
            ecgFileLink = "",
            PhysicalExamination = "",
            LabotryRadiology = "",
            ImpressionPlan = "",
            questionerAnswers = "",
            remarks = "",
            location = "${location?.city}, ${location?.postalCode}, ${location?.address}"
        )
        createNewSession(emptySession)
    }


    fun updateSession(session: Session){
        APIManager.shared.updatePatientSession(session)
    }

    fun createSession(session: Session){
        createNewSession(session)
    }


    fun parseImageList(optionList : String) : MutableList<ImageWithCaptions>{
        val reminderRegex = """ImageWithCaptions\(([^)]+)\)""".toRegex()
        val reminderMatches = reminderRegex.findAll(optionList)
        val imageList = ArrayList<ImageWithCaptions>()
        for (match in reminderMatches) {
            val properties = match.groupValues[1].split(", ")
            var caption = ""
            var imageLink = ""
            for (property in properties) {
                val keyValue = property.split("=")
                val key = keyValue[0]
                val values = keyValue[1]
                when (key) {
                    "caption" -> caption = values
                    "imageLink" -> imageLink = values
                }
            }
            val reminder = ImageWithCaptions(caption, imageLink)
            imageList.add(reminder)
        }
        return imageList
    }

    fun updatePEAttachments(session: Session){

    }


    companion object {

        @Volatile private var instance: PatientSessionManagerRepo? = null

        fun getInstance() = instance ?: synchronized(this) {
                instance ?: PatientSessionManagerRepo().also { instance = it }
        }
    }
}