package com.aarogyaforworkers.aarogyaFDC.composeScreens


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital

import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem


var isPMSHSetUpDone = false
var isFromPMSHSave = false
var isPMSHDoneClick = false
var isFromCamera = false

@Composable
fun PastMedicalSurgicalHistoryScreen(navHostController: NavHostController){
    Disableback()

    val isUpdating = remember { mutableStateOf(false) }

    var pastMediSurgHis = MainActivity.subUserRepo.isTempPopUpText

    var showPicUploadAlert = remember { mutableStateOf(false) }

    val onDonePressed= remember { mutableStateOf(false) }

    val selectedUser = MainActivity.adminDBRepo.getSelectedSubUserProfile().copy()

    val parsedText = selectedUser.PastMedicalSurgicalHistory.split("-:-")


    if(!isPMSHSetUpDone){
        pastMediSurgHis.value = pastMediSurgHis.value
        if(parsedText.filter { it.isNotEmpty() }.isEmpty()) {
            isPMSHSetUpDone = true
        }
    }

    if(parsedText.size == 2 && !isPMSHSetUpDone){
        pastMediSurgHis.value = parsedText.first()
        isPMSHSetUpDone = true
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

//    val user = MainActivity.adminDBRepo.getSelectedSubUserProfile().copy()

    when(MainActivity.adminDBRepo.subUserProfileCreateUpdateState.value){
        true -> {
            if(MainActivity.adminDBRepo.getLoggedInUser().groups.isEmpty()){
                MainActivity.adminDBRepo.searchUserByQuery(selectedUser.first_name.toCharArray().first().toString(), MainActivity.adminDBRepo.getLoggedInUser().admin_id)
            }else{
                MainActivity.adminDBRepo.searchUserByQuery(selectedUser.first_name.toCharArray().first().toString(), MainActivity.adminDBRepo.getLoggedInUser().groups)
            }
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(false)
            if(isFromPMSHSave || isPMSHDoneClick) {
                MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
            }
            isUpdating.value = false

            if(isPMSHDoneClick){
                navHostController.navigate(Destination.UserHome.routes)
            }
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(null)

        }
        false -> {
            MainActivity.adminDBRepo.updateSubUserProfileCreateUpdateState(null)


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
                navHostController.navigate(Destination.UserHome.routes)
                MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
            },
            onNoClick = { onDonePressed.value=false }) {
        }
    }

    Column(Modifier.fillMaxSize()){
        Spacer(modifier = Modifier.height(15.dp))
        TopBarWithBackTitle(onBackClick = {
            if(MainActivity.subUserRepo.anyUpdateThere.value) {
                onDonePressed.value = true
            } else {
    //                MainActivity.subUserRepo.updateEditTextEnable(false)
                navHostController.navigate(Destination.UserHome.routes)
            } },
            title = "Past Medical & \nSurgical History",
//            onSaveClick = {
//                //on save click
//                isFromCamera = false
//                isUpdating.value = true
//                isFromPMSHSave = true
//                val text = pastMediSurgHis.value
//                val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
//                selectedUser.PastMedicalSurgicalHistory = "${text}-:-${newUpdatedList}"
//
//                MainActivity.adminDBRepo.adminUpdateSubUser(user = selectedUser)
//                MainActivity.adminDBRepo.setNewSubUserprofile(selectedUser.copy())
//                MainActivity.adminDBRepo.setNewSubUserprofileCopy(selectedUser.copy())
//                //MainActivity.sessionRepo.updateSession(selectedSession)
//            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        LazyColumn(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)){
            item {
                InputTextField(
                    textInput = pastMediSurgHis.value,
                    onChangeInput = { newValue ->
                        pastMediSurgHis.value = newValue
                        MainActivity.subUserRepo.updateTempPopUpText(pastMediSurgHis.value)
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
                        MainActivity.cameraRepo.updateSavedImageView(AttachmentPreviewItem(
                            item.caption,
                            item.imageLink
                        ))
                        if(MainActivity.cameraRepo.downloadedImagesMap.value.keys.contains(item.imageLink)){
                            MainActivity.cameraRepo.updateSelectedImage(MainActivity.cameraRepo.downloadedImagesMap.value[item.imageLink])
                        }else{
                            MainActivity.cameraRepo.updateSelectedImage(null)
                        }
                        MainActivity.cameraRepo.updateAttachmentScreenNo("PMSH")
                        navHostController.navigate(Destination.SavedImagePreviewScreen2.routes)
                    }) { attachment ->
                        // Delete
                        val list = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().filter { it != attachment }
                        // update the list ->
                        isUpdating.value = true
                        val newList = list.toString()
                        val title = selectedUser!!.PastMedicalSurgicalHistory.split("-:-")
                        selectedUser.PastMedicalSurgicalHistory = "${title.first()}-:-${newList}"
                        MainActivity.sessionRepo.clearImageList()
                        list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }

                        MainActivity.adminDBRepo.adminUpdateSubUser(user = selectedUser)
                        MainActivity.adminDBRepo.setNewSubUserprofile(selectedUser.copy())
                        MainActivity.adminDBRepo.setNewSubUserprofileCopy(selectedUser.copy())
//                        MainActivity.sessionRepo.updateSession(selectedSession)
                    }
                }

                LoadImagesSequentially(images = imageList, onImageDownloaded = {
                    Log.d("TAG", "LoadImageFromUrl: downloaded image ${it.byteCount} ")
//                    MainActivity.cameraRepo.updateDownloadedImage(it)
                })

                Spacer(modifier = Modifier.height(15.dp))

                PhotoBtn {
                    //on photoBtnClick
                    isFromCamera = true
                    isFromPMSHSave = false
                    MainActivity.cameraRepo.updateAttachmentScreenNo("PMSH")
                    navHostController.navigate(Destination.Camera.routes)
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)) {
            PopUpBtnSingle(btnName = "Done",
                onBtnClick = { //on save click
                    isFromCamera = false
                    isPMSHDoneClick = true
                    isUpdating.value = true
                    isFromPMSHSave = true
                    val text = pastMediSurgHis.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedUser.PastMedicalSurgicalHistory = "${text}-:-${newUpdatedList}"
                    MainActivity.adminDBRepo.adminUpdateSubUser(user = selectedUser)
                    MainActivity.adminDBRepo.setNewSubUserprofile(selectedUser.copy())
                    MainActivity.adminDBRepo.setNewSubUserprofileCopy(selectedUser.copy())
//                    MainActivity.sessionRepo.updateSession(selectedSession)
                             },
                Modifier.fillMaxWidth()
            )
        }
    }
    if(isUpdating.value) showProgress()
}

