package com.aarogyaforworkers.aarogyaFDC.PatientSession

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.DocumentInfo
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.ImageWithCaptions
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.Options
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.Pdf
import com.aarogyaforworkers.awsapi.APIManager
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class PatientSessionManagerRepo {

    var selectedsession : Session? = null

    var documentInfoList = mutableStateListOf<DocumentInfo>()

    private val imageWithCaptions : ImageWithCaptions? = null

    private val pdf : Pdf? = null

    var listState : MutableState<LazyListState?> = mutableStateOf(null)

    var scrollToIndex : MutableState<Int> = mutableStateOf(-1)

    private var isimageWithCaptionsList = mutableStateOf(mutableListOf(imageWithCaptions))

    private var isPdfList = mutableStateOf(mutableListOf(pdf))

    var imageWithCaptionsList : MutableState<MutableList<ImageWithCaptions?>> = isimageWithCaptionsList

    var pdfList : MutableState<MutableList<Pdf?>> = isPdfList

    private var isAttachmentUploaded : MutableState<Boolean?> = mutableStateOf(null)

    var attachmentUploadedStatus : State<Boolean?> = isAttachmentUploaded

    var knownOffset = 1550 // Adjust this value as needed

    var isDownloading = mutableStateOf(false)

    fun updateAttachmentUploadedStatus(isUploaded : Boolean?){
        isAttachmentUploaded.value = isUploaded
    }
    fun updateImageWithCaptionList(imageWithCaptions: ImageWithCaptions){
        if(!isimageWithCaptionsList.value.contains(imageWithCaptions)){
            isimageWithCaptionsList.value.add(imageWithCaptions)
        }
    }

    fun updatePdfList(pdf: Pdf){
        if(!isPdfList.value.contains(pdf)){
            isPdfList.value.add(pdf)
        }
    }

    fun clearImageList(){
        isimageWithCaptionsList.value = arrayListOf()
    }

    fun clearPdfList(){
        isPdfList.value = arrayListOf()
    }

    private var isSessionCreated : MutableState<Boolean?> = mutableStateOf(null)

    var sessionCreatedStatus : State<Boolean?> = isSessionCreated

    private var isSessionDeleted : MutableState<Boolean?> = mutableStateOf(null)

    var sessionDeletedStatus : State<Boolean?> = isSessionDeleted

    private var isFetchingSession : MutableState<Boolean?> = mutableStateOf(null)

    var fetchingSessionState : State<Boolean?> = isFetchingSession

    fun updateSessionFetchStatus(status : Boolean?){
        isFetchingSession.value = status
    }

    fun updateSessionDeletedStatus(status: Boolean?){
        isSessionDeleted.value = status
    }

    private var isFetching : MutableState<Boolean> = mutableStateOf(false)

    var fetching : State<Boolean> = isFetching

    fun updateSessionFetch(status : Boolean){
        isFetching.value = status
    }

    fun updateIsSessionCreatedStatus(isCreated : Boolean?){
        isSessionCreated.value = isCreated
    }

    private var isSessionUpdated : MutableState<Boolean?> = mutableStateOf(null)

    var sessionUpdatedStatus : State<Boolean?> = isSessionUpdated

    fun updateIsSessionUpdatedStatus(isUpdated : Boolean?){
        isSessionUpdated.value = isUpdated
    }

    fun createNewSession(session: Session) = APIManager.shared.createPatientSession(session)

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewEmptySessionForUser(userId : String){
        MainActivity.pc300Repo.updateDateTime()
        val currentDateTime = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val currentDate = currentDateTime.format(dateFormatter)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val currentTime = currentDateTime.format(timeFormatter)
        if(MainActivity.pc300Repo.deviceId.isEmpty()) MainActivity.pc300Repo.deviceId = "XXXXXXXX"
        val sessionId = MainActivity.pc300Repo.getSessionTime().replace(":", "")+":"+MainActivity.pc300Repo.deviceId.takeLast(4).replace(":", "")+":"+MainActivity.adminDBRepo.getLoggedInUser().admin_id.takeLast(6)
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
            location = "${location?.city}, ${location?.postalCode}, ${location?.address}, ${location?.country}, ${location?.lat}, ${location?.lon}",
            nextVisit = ""
        )

        createNewSession(emptySession)
    }

    fun updateSession(session: Session){
        APIManager.shared.updatePatientSession(session)
    }

    fun createSession(session: Session){
        if(MainActivity.pc300Repo.isEcgDataTaken && MainActivity.csvRepository.getSessionFile() != null) {
            // start waiting for session upload status
            MainActivity.s3Repo.startUploadingFile(MainActivity.csvRepository.getSessionFile()!!)
        }else{
            createNewSession(session)
        }
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

    fun parsePdfList(optionList: String): MutableList<Pdf> {
        val pdfRegex = """Pdf\(([^)]+)\)""".toRegex()
        val pdfMatches = pdfRegex.findAll(optionList)
        val pdfList = mutableListOf<Pdf>()
        for (match in pdfMatches) {
            val properties = match.groupValues[1].split(", ")
            var name = ""
            var pdfLink = ""

            for (property in properties) {
                val keyValue = property.split("=")
                val key = keyValue[0]
                val value = keyValue[1]
                when (key) {
                    "name" -> name = value
                    "pdfLink" -> pdfLink = value
                }
            }

            val pdf = Pdf(name, pdfLink)
            pdfList.add(pdf)
        }
        return pdfList
    }

    companion object {

        @Volatile private var instance: PatientSessionManagerRepo? = null

        fun getInstance() = instance ?: synchronized(this) {
                instance ?: PatientSessionManagerRepo().also { instance = it }
        }
    }
}