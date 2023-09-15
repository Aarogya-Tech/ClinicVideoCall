package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentRowItem

var isLRSetUpDone = false
var isFromLRSave = false
var isLRDoneClick = false

@Composable
fun LaboratoryRadioLogyScreen(navHostController: NavHostController){
    Disableback()

    val isEditable = MainActivity.subUserRepo.isEditTextEnable

    var isUpdating = remember { mutableStateOf(false) }

    var labRadio = MainActivity.subUserRepo.isTempPopUpText

    var showPicUploadAlert = remember { mutableStateOf(false) }

    val onDonePressed= remember { mutableStateOf(false) }

    var selectedSession = MainActivity.sessionRepo.selectedsession

    val parsedText = selectedSession!!.LabotryRadiology.split("-:-")

    if(!isLRSetUpDone){
        labRadio.value = ""
    }

    if(parsedText.size == 2 && !isLRSetUpDone){
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
    }

    if (showPicUploadAlert.value){
        ImagePickerDialog(onCancelClick = { /*TODO*/ }, onGalleryClick = { /*TODO*/ }) {
            navHostController.navigate(Destination.Camera.routes)
        }
    }


    when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

        true -> {
            isUpdating.value = false
            MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
            if(isFromLRSave) MainActivity.subUserRepo.updateEditTextEnable(false)
            MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
            if(isLRDoneClick) navHostController.navigate(Destination.UserHome.routes)
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
                MainActivity.subUserRepo.updateEditTextEnable(false)
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
            TopBarWithBackEditBtn(onBackClick = {
                if(MainActivity.subUserRepo.anyUpdateThere.value) {
                    onDonePressed.value = true
                }
                else {
                    MainActivity.subUserRepo.updateEditTextEnable(false)
                    navHostController.navigate(Destination.UserHome.routes)
                } },
                title = "Laboratory & Radiology",
                onSaveClick = {
                    //on save btn click
                    isFromLRSave = true
                    val text = labRadio.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession.LabotryRadiology = "${text}-:-${newUpdatedList}"
                    isUpdating.value = true
                    MainActivity.sessionRepo.updateSession(selectedSession)
                } )
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
                        // delete
                        val list = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().filter { it != attachment }
                        // update the list ->
                        isUpdating.value = true
                        val selectedSession = MainActivity.sessionRepo.selectedsession
                        val newList = list.toString()
                        selectedSession!!.LabotryRadiology = "${labRadio.value}-:-$newList"
                        MainActivity.sessionRepo.clearImageList()
                        list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }
                        MainActivity.sessionRepo.updateSession(selectedSession)
                    }
                }

                LoadImagesSequentially(images = imageList, onImageDownloaded = {
                    Log.d("TAG", "LoadImageFromUrl: downloaded image ${it.byteCount} ")
//                    MainActivity.cameraRepo.updateDownloadedImage(it)
                })

                Spacer(modifier = Modifier.height(15.dp))



                PhotoBtn {
                    //on photoBtnClick
                    isFromLRSave = false
                    MainActivity.cameraRepo.updateAttachmentScreenNo("LR")
                    navHostController.navigate(Destination.Camera.routes)
                }

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
                    selectedSession.LabotryRadiology = "${text}-:-${newUpdatedList}"
                    MainActivity.sessionRepo.clearImageList()
                    navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                }, Modifier.fillMaxWidth())
            }else{
                PopUpBtnSingle(btnName = "Done",
                    onBtnClick = { //on save btn click
                        isLRDoneClick = true
                        isFromLRSave = true
                    val text = labRadio.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession.LabotryRadiology = "${text}-:-${newUpdatedList}"
                    isUpdating.value = true
                    MainActivity.sessionRepo.updateSession(selectedSession) }, Modifier.fillMaxWidth())
//                PopBtnDouble(
//                    btnName1 = "Save",
//                    btnName2 = "Done",
//                    onBtnClick1 = {
//                        //on save btn click
//                        isFromLRSave = true
//                        val text = labRadio.value
//                        val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
//                        selectedSession.LabotryRadiology = "${text}-:-${newUpdatedList}"
//                        isUpdating.value = true
//                        MainActivity.sessionRepo.updateSession(selectedSession)
//                    },
//                    onBtnClick2 = { //on done btn click
//                        if(isEditable.value){
//                            onDonePressed.value=true
//                        } else {
//                            navHostController.navigate(Destination.UserHome.routes)
//                        } },
//                    enable = isEditable.value
//                )
            }
        }
    }
    if(isUpdating.value) showProgress()

}