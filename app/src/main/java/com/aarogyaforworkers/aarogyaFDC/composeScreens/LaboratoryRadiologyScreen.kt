package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.DocumentInfo
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.ImageWithCaptions
import downloadFile
import openDocuments
import java.io.ByteArrayOutputStream
import java.io.File

var isLRSetUpDone = false
var isFromLRSave = false
var isLRDoneClick = false

@Composable
fun LaboratoryRadioLogyScreen(navHostController: NavHostController){
    Disableback()

     // val isEditable = MainActivity.subUserRepo.isEditTextEnable


    var isUpdating = remember { mutableStateOf(false) }

    var labRadio = MainActivity.subUserRepo.isTempPopUpText

    var showPicUploadAlert = remember { mutableStateOf(false) }

    val onDonePressed= remember { mutableStateOf(false) }

    var selectedSession = MainActivity.sessionRepo.selectedsession

    val parsedText = selectedSession!!.LabotryRadiology.split("-:-")

    if(!isLRSetUpDone){
        labRadio.value = ""
        if(parsedText.filter { it.isNotEmpty() }.isEmpty()) {
            isLRSetUpDone = true
        }
    }

    when(MainActivity.adminDBRepo.pdfUploadstate.value){

        true -> {
            isUpdating.value = false
            MainActivity.adminDBRepo.updatePDfUploadState(null)
        }

        false -> {
            isUpdating.value = false
            MainActivity.adminDBRepo.updatePDfUploadState(null)
        }

        null -> {

        }
    }

    if(parsedText.size > 2 && !isLRSetUpDone){
        labRadio.value = parsedText.first()
        isLRSetUpDone = true
        val listIOfImages = MainActivity.sessionRepo.parseImageList(parsedText[1])
        if(listIOfImages.isEmpty()){
            MainActivity.sessionRepo.clearImageList()
        }else{
            listIOfImages.forEach {
                MainActivity.sessionRepo.updateImageWithCaptionList(it)
            }
        }

        if(parsedText.size == 3){
            val listOfPdf = MainActivity.sessionRepo.parsePdfList(parsedText[2])
            if(listOfPdf.isEmpty()){
                MainActivity.sessionRepo.clearPdfList()
            }else{
                listOfPdf.forEach {
                    MainActivity.sessionRepo.updatePdfList(it)
                }
            }
        }
    }



    if (showPicUploadAlert.value){
        ImagePickerDialog(onCancelClick = { /*TODO*/ }, onGalleryClick = { /*TODO*/ }) {
            navHostController.navigate(Destination.Camera.routes)
        }
    }


    when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

        true -> {
//            MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
            if(isFromLRSave || isLRDoneClick) {
                MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
            }
            isUpdating.value = false
            if(isLRDoneClick) {
                navHostController.navigate(Destination.UserHome.routes)
            }
        }

        false -> {
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
        }

        null -> {

        }

    }

    if(onDonePressed.value)
    {
        AlertView(
            showAlert = true,
            title = "Do you want to go back?",
            subTitle = "You have unsaved changes.Your changes will be discarded if you press Yes.",
            subTitle1 = "",
            onYesClick = {
                MainActivity.sessionRepo.clearPdfList()
                MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
                navHostController.navigate(Destination.UserHome.routes) },
            onNoClick = { onDonePressed.value=false }) {
        }
    }

    Column(Modifier
        .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        if(isFromVital){
            TopBarWithEditBtn(title = "Laboratory & Radiology")
        } else{
            TopBarWithBackTitle(onBackClick = {
                if(MainActivity.subUserRepo.anyUpdateThere.value) {
                    onDonePressed.value = true
                } else {
                    MainActivity.sessionRepo.clearPdfList()
                    navHostController.navigate(Destination.UserHome.routes)
                } },
                title = "Laboratory & Radiology",
//                onSaveClick = {
//                    //on save btn click
//                    isFromLRSave = true
//                    val text = labRadio.value
//                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
//                    val newUpdatedPdfList = MainActivity.sessionRepo.pdfList.value.filterNotNull().toString()
//                    selectedSession.LabotryRadiology = "${text}-:-${newUpdatedList}-:-${newUpdatedPdfList}"
//                    isUpdating.value = true
//                    isLRSetUpDone = false
//                    MainActivity.sessionRepo.updateSession(selectedSession)
//                }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        LazyColumn(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)){
            item {
                InputTextField(
                    textInput = labRadio.value,
                    onChangeInput = { newValue ->
                        labRadio.value = newValue
                        MainActivity.subUserRepo.updateTempPopUpText(labRadio.value)
                        MainActivity.subUserRepo.updateIsAnyUpdateThere(true)
                    },
                    placeholder = "Please Enter Details",
                    keyboard = KeyboardType.Text,
                    TestTag = ""
                )

                Spacer(modifier = Modifier.height(15.dp))

                RegularTextView(title = "PDF Document", modifier = Modifier.padding(horizontal = 16.dp), fontSize = 16)

                Spacer(modifier = Modifier.height(15.dp))

                val context= LocalContext.current

                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val data: Uri? = result.data?.data
                        if (data != null) {
                            val contentResolver = context.contentResolver
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            val inputStream = contentResolver.openInputStream(data)
                            if (inputStream != null) {
                                val buffer = ByteArray(1024)
                                var len: Int
                                while (inputStream.read(buffer).also { len = it } != -1) {
                                    byteArrayOutputStream.write(buffer, 0, len)
                                }
                                isUpdating.value = true
                                val byteArray = byteArrayOutputStream.toByteArray()
                                val documentName = getDocumentName(context, data)
                                MainActivity.adminDBRepo.uploadPdfDoc(byteArray, documentName ?: "")
                                MainActivity.sessionRepo.documentInfoList.add(DocumentInfo(documentName!!, data))
                                inputStream.close()
                            } else {
                                // Handle the case where the inputStream is null (failed to open the file)
                            }
                        } else {
                            // Handle the case where the data URI is null
                        }
                    }
                }

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/pdf"
                }
                val docList = MainActivity.sessionRepo.pdfList.value.filterNotNull()

                docList.forEach {item ->
                    AttachmentRow(
                        attachment = ImageWithCaptions("",""),
                        btnName = item.name,
                        onBtnClick = {
                            val pdf = MainActivity.sessionRepo.documentInfoList.filter { it.name == item.name }
                            if(pdf.isNotEmpty()){
                                openDocument(context, pdf.first().uri)
                            }else{
                                // download PDF ->
                                val destinationFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), item.name)
                                val pdflink = item.pdfLink
                                isUpdating.value = true
                                downloadFile(pdflink, destinationFile,
                                    onComplete = { downloadedUri ->
                                        // File downloaded successfully, now open it
                                        openDocuments(context, downloadedUri)
                                        isUpdating.value = false
                                    },
                                    onError = { error ->
                                        isUpdating.value = false
                                        Toast.makeText(context, "can't download try again", Toast.LENGTH_SHORT).show()
                                    }
                                )

                            }
                        },
                        onDeleteClick = { pdf ->

                            isUpdating.value = true

                            val newList = MainActivity.sessionRepo.pdfList.value.filterNotNull().filter { it != item }

                            val stringList = newList.toString()

                            val title = selectedSession.LabotryRadiology.split("-:-")

                            val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()

                            selectedSession.LabotryRadiology = "${title.first()}-:-${newUpdatedList}-:-${stringList}"

                            MainActivity.sessionRepo.clearPdfList()

                            newList.forEach { MainActivity.sessionRepo.updatePdfList(it) }

                            MainActivity.sessionRepo.updateSession(selectedSession)
                        }
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                }

                Row(Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)){
                    Button(onClick = {
                        launcher.launch(intent)
                    },
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2f5597)
                        ),
                        modifier = Modifier.width(250.dp)
                    ) {
                        RegularTextView(title = "Attach a new PDF", textColor = Color.White, fontSize = 16)
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Divider(
                    color = Color.LightGray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                RegularTextView(title = "Images", modifier = Modifier.padding(horizontal = 16.dp), fontSize = 16)

                val imageList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull()

                imageList.forEach { item->
                    Spacer(modifier = Modifier.height(15.dp))
                    AttachmentRow(attachment = item, btnName = item.caption, onBtnClick = {
                        MainActivity.cameraRepo.updateSavedImageView(
                            AttachmentPreviewItem(
                            item.caption,
                            item.imageLink
                        ))
                        if(MainActivity.cameraRepo.downloadedImagesMap.value.keys.contains(item.imageLink)){
                            MainActivity.cameraRepo.updateSelectedImage(MainActivity.cameraRepo.downloadedImagesMap.value[item.imageLink])
                        }else{
                            MainActivity.cameraRepo.updateSelectedImage(null)
                        }
                        MainActivity.cameraRepo.updateAttachmentScreenNo("LR")
                        navHostController.navigate(Destination.SavedImagePreviewScreen2.routes)
                    }) { attachment ->
                        //delete btn click
                        val list = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().filter { it != attachment }
                        // update the list ->
                        isUpdating.value = true
                        val selectedSession = MainActivity.sessionRepo.selectedsession
                        val newList = list.toString()
                        val title = selectedSession!!.LabotryRadiology.split("-:-")
                        val newUpdatedPdfList = MainActivity.sessionRepo.pdfList.value.filterNotNull().toString()
                        selectedSession.LabotryRadiology = "${title.first()}-:-${newList}-:-${newUpdatedPdfList}"
                        MainActivity.sessionRepo.clearImageList()
                        list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }
                        MainActivity.sessionRepo.updateSession(selectedSession)
                    }
                }

                LoadImagesSequentially(images = imageList, onImageDownloaded = {
                    Log.d("TAG", "LoadImageFromUrl: downloaded image ${it.byteCount} ")
                })

                Spacer(modifier = Modifier.height(15.dp))

                PhotoBtn {
                    //on photoBtnClick
                    isFromLRSave = false
                    MainActivity.cameraRepo.updateAttachmentScreenNo("LR")
                    navHostController.navigate(Destination.Camera.routes)
                }

                Spacer(modifier = Modifier.height(15.dp))


            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)) {
            if (isFromVital){
                PopUpBtnSingle(btnName = "Next", {
                    val text = labRadio.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    val newUpdatedPdfList = MainActivity.sessionRepo.pdfList.value.filterNotNull().toString()
                    selectedSession.LabotryRadiology = "${text}-:-${newUpdatedList}-:-${newUpdatedPdfList}"
                    MainActivity.sessionRepo.clearImageList()
                    MainActivity.sessionRepo.clearPdfList()
                    navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                }, Modifier.fillMaxWidth())
            }else{
                PopUpBtnSingle(btnName = "Done",
                    onBtnClick = { //on save btn click
                        isLRDoneClick = true
                        isFromLRSave = true
                    val text = labRadio.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    val newUpdatedPdfList = MainActivity.sessionRepo.pdfList.value.filterNotNull().toString()
                        selectedSession.LabotryRadiology = "${text}-:-${newUpdatedList}-:-${newUpdatedPdfList}"
                    isUpdating.value = true
                    MainActivity.sessionRepo.updateSession(selectedSession) }, Modifier.fillMaxWidth())
            }
        }
    }

    if(isUpdating.value) showProgress()
}


private fun getDocumentName(context: Context, uri: Uri): String? {
    var documentName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        documentName = cursor.getString(nameIndex)
    }
    return documentName
}

fun openDocument(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "application/pdf")
    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    context.startActivity(intent)
}