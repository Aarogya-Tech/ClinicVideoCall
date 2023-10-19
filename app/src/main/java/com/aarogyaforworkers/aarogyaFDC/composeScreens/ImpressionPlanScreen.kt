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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Commons.selectedSession
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem

var isIPSetUpDone = false
var isFromIPSave = false
var isIPDoneClick = false

@Composable
fun ImpressionPlanScreen(navHostController: NavHostController){

    Disableback()

    val impressionPlan = MainActivity.subUserRepo.isTempPopUpText

    val isUpdating = remember { mutableStateOf(false) }

    val showPicUploadAlert = remember { mutableStateOf(false) }

    val onDonePressed= remember { mutableStateOf(false) }

    val selectedSession_Imp = MainActivity.sessionRepo.selectedsession

    val parsedText = selectedSession_Imp!!.ImpressionPlan.split("-:-")

    if(!isIPSetUpDone){
        impressionPlan.value = ""
        if(parsedText.filter { it.isNotEmpty() }.isEmpty()) {
            isIPSetUpDone = true
        }
    }

    if(parsedText.size == 2 && !isIPSetUpDone){
        impressionPlan.value = parsedText.first()
        isIPSetUpDone = true
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

    val context = LocalContext.current



    when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

        true -> {
//            MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
            if(isFromIPSave || isIPDoneClick) {
                MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
            }
            isUpdating.value = false
            if(isIPDoneClick) {
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
//                MainActivity.subUserRepo.updateEditTextEnable(false)
                MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
                navHostController.navigate(Destination.UserHome.routes) },
            onNoClick = { onDonePressed.value=false }) {
        }
    }

    if (showPicUploadAlert.value){
        ImagePickerDialog(onCancelClick = { /*TODO*/ }, onGalleryClick = { /*TODO*/ }) {
            navHostController.navigate(Destination.Camera.routes)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        if(isFromVital){
            TopBarWithEditBtn(title = "Impression & Plan")
        } else{
            TopBarWithBackTitle(onBackClick = {
                if(MainActivity.subUserRepo.anyUpdateThere.value) {
                    onDonePressed.value = true
                } else {
        //                    MainActivity.subUserRepo.updateEditTextEnable(false)
                    navHostController.navigate(Destination.UserHome.routes)
                } },
                title = "Impression & Plan",
//                onSaveClick = {
//                    //on save btn click
//        //                    navHostController.navigate(Destination.DateAndTimePickerScree.routes)
//                    isFromIPSave = true
//                    val text = impressionPlan.value
//                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
//                    selectedSession_Imp.ImpressionPlan = "${text}-:-${newUpdatedList}"
//                    isUpdating.value = true
//                    MainActivity.sessionRepo.updateSession(selectedSession_Imp)
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
                    textInput = impressionPlan.value,
                    onChangeInput = { newValue ->
                        impressionPlan.value = newValue
                        MainActivity.subUserRepo.updateTempPopUpText(impressionPlan.value)
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
                        MainActivity.cameraRepo.updateAttachmentScreenNo("IP")
                        navHostController.navigate(Destination.SavedImagePreviewScreen2.routes)
                    }) { attachment ->
                        //delete btn click
                        val list = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().filter { it != attachment }
                        // update the list ->
                        isUpdating.value = true
                        val selectedSession = MainActivity.sessionRepo.selectedsession
                        val newList = list.toString()
                        val title = selectedSession!!.ImpressionPlan.split("-:-")
                        selectedSession.ImpressionPlan = "${title.first()}-:-${newList}"
//                        selectedSession!!.ImpressionPlan = "${impressionPlan.value}-:-$newList"
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
                    isFromIPSave = false
                    MainActivity.cameraRepo.updateAttachmentScreenNo("IP")
                    navHostController.navigate(Destination.Camera.routes)
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)) {
            if (isFromVital){
                PopUpBtnSingle(btnName = "Done", {
                    isFromIPSave = true
                    val text = impressionPlan.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession_Imp.ImpressionPlan = "${text}-:-${newUpdatedList}"
                    MainActivity.sessionRepo.selectedsession = selectedSession_Imp
                    navHostController.navigate(Destination.SetCalanderScreen.routes)
                }, Modifier.fillMaxWidth())
            }else{
                PopUpBtnSingle(btnName = "Done",
                    onBtnClick = { //on save btn click
                        isIPDoneClick = true
                        isFromIPSave = true
                        val text = impressionPlan.value
                        val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                        selectedSession_Imp.ImpressionPlan = "${text}-:-${newUpdatedList}"
                        isUpdating.value = true
                        MainActivity.sessionRepo.updateSession(selectedSession)
                },Modifier.fillMaxWidth())
            }
        }
    }
    if(isUpdating.value) showProgress()

}