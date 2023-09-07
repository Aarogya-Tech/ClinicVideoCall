package com.aarogyaforworkers.aarogyaFDC.composeScreens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem

@Composable
fun ImpressionPlanScreen(navHostController: NavHostController){

    val isEditable = remember { mutableStateOf(false) }

    val impressionPlan = remember { mutableStateOf("") }

    val isUpdating = remember { mutableStateOf(false) }

    val showPicUploadAlert = remember { mutableStateOf(false) }

    val selectedSession = MainActivity.sessionRepo.selectedsession

    if(isFromVital) isEditable.value = true

    val parsedText = selectedSession!!.ImpressionPlan.split("-:-")

    impressionPlan.value = parsedText.first()

    if(parsedText.size == 2){
        val listIOfImages = MainActivity.sessionRepo.parseImageList(parsedText[1])
        listIOfImages.forEach {
            MainActivity.sessionRepo.updateImageWithCaptionList(it)
        }
    }

    if(isFromVital){

        when(MainActivity.sessionRepo.sessionCreatedStatus.value){

            true -> {
                isUpdating.value = false
                isEditable.value = false
                MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
                navHostController.navigate(Destination.UserHome.routes)
                MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
            }

            false -> {
                MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
            }

            null -> {

            }
        }

    }


    when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

        true -> {
            isUpdating.value = false
            MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
            isEditable.value = false
            // refresh session list
        }

        false -> {

            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)

        }

        null -> {

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
            TopBarWithBackEditBtn(onBackClick = { navHostController.navigate(Destination.UserHome.routes) }, title = "Impression & Plan", isEditable = isEditable)
        }

        Spacer(modifier = Modifier.height(40.dp))

        LazyColumn(Modifier.weight(1f)){
            item {
                InputTextField(
                    textInput = impressionPlan.value,
                    onChangeInput = { newValue ->
                        impressionPlan.value = newValue
                    },
                    placeholder = "Please Enter Details",
                    keyboard = KeyboardType.Text,
                    enable = isEditable.value,
                    TestTag = ""
                )

                val imageList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull()

                imageList.forEach { item->
                    Spacer(modifier = Modifier.height(15.dp))
                    AttachmentRow(btnName = item.caption, onBtnClick = {
                        MainActivity.cameraRepo.updateSavedImageView(
                            AttachmentPreviewItem(
                            item.caption,
                            item.imageLink
                        ))
                        MainActivity.cameraRepo.updateAttachmentScreenNo("IP")
                        navHostController.navigate(Destination.SavedImagePreviewScreen.routes)
                    }) {

                    }
                }

                Spacer(modifier = Modifier.height(15.dp))



                PhotoBtn {
                    //on photoBtnClick
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
                PopUpBtnSingle(btnName = "Done") {
                    val text = impressionPlan.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession.ImpressionPlan = "${text}-:-${newUpdatedList}"
                    isUpdating.value = true
                    MainActivity.sessionRepo.createSession(selectedSession)
//                  navHostController.navigate(Destination.UserHome.routes)
                }
            }else{
                PopBtnDouble(btnName1 = "Save", btnName2 = "Done", onBtnClick1 = {
                    //on save btn click
                    val text = impressionPlan.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession.ImpressionPlan = "${text}-:-${newUpdatedList}"
                    isUpdating.value = true
                    MainActivity.sessionRepo.updateSession(selectedSession)
                }) {
                    //on done btn click
                    navHostController.navigate(Destination.UserHome.routes)
                }
            }
        }
    }
    if(isUpdating.value) showProgress()

}