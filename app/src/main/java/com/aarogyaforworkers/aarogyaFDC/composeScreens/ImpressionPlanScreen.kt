package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem

var isIPSetUpDone = false

@Composable
fun ImpressionPlanScreen(navHostController: NavHostController){
    Disableback()


    val isEditable = MainActivity.subUserRepo.isEditTextEnable

    val impressionPlan = MainActivity.subUserRepo.isTempPopUpText

    val isUpdating = remember { mutableStateOf(false) }

    val showPicUploadAlert = remember { mutableStateOf(false) }

    val onDonePressed= remember { mutableStateOf(false) }

    val selectedSession = MainActivity.sessionRepo.selectedsession

    val parsedText = selectedSession!!.ImpressionPlan.split("-:-")

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

    if(isFromVital){

        when(MainActivity.sessionRepo.sessionCreatedStatus.value){

            true -> {
                MainActivity.pc300Repo.clearSessionValues()
                MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
                isUpdating.value = false
                MainActivity.subUserRepo.updateEditTextEnable(false)
                navHostController.navigate(Destination.UserHome.routes)
                isSessionPlayedOnUserHome = false
                MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
            }

            false -> {

                isUpdating.value = false

                isSessionPlayedOnUserHome = false


                Toast.makeText(context, "Something went wrong please try again", Toast.LENGTH_SHORT).show()

                MainActivity.sessionRepo.updateIsSessionCreatedStatus(null)
            }

            null -> {

            }
        }

    }


    when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

        true -> {
            MainActivity.subUserRepo.getSessionsByUserID(userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id)
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
            isUpdating.value = false
            MainActivity.subUserRepo.updateEditTextEnable(false)        }

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
            TopBarWithBackEditBtn(onBackClick = {
                if(isEditable.value) {
                    onDonePressed.value = true
                }
                else {
                    MainActivity.subUserRepo.updateEditTextEnable(false)
                    navHostController.navigate(Destination.UserHome.routes)
                } },
                title = "Impression & Plan",
                isEditable = isEditable)
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
                    },
                    placeholder = "Please Enter Details",
                    keyboard = KeyboardType.Text,
                    enable = isEditable.value,
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
                        MainActivity.cameraRepo.updateAttachmentScreenNo("IP")
                        navHostController.navigate(Destination.SavedImagePreviewScreen.routes)
                    }) { attachment ->
                        val list = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().filter { it != attachment }
                        // update the list ->
                        isUpdating.value = true
                        val selectedSession = MainActivity.sessionRepo.selectedsession
                        val newList = list.toString()
                        selectedSession!!.ImpressionPlan = "${impressionPlan.value}-:-$newList"
                        MainActivity.sessionRepo.clearImageList()
                        list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }
                        MainActivity.sessionRepo.updateSession(selectedSession)
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
                    MainActivity.sessionRepo.clearImageList()
                    MainActivity.sessionRepo.createSession(selectedSession)
                }
            }else{

                PopBtnDouble(
                    btnName1 = "Save",
                    btnName2 = "Done",
                    onBtnClick1 = {
                        //on save btn click
                        val text = impressionPlan.value
                        val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                        selectedSession.ImpressionPlan = "${text}-:-${newUpdatedList}"
                        isUpdating.value = true
                        MainActivity.sessionRepo.updateSession(selectedSession) },
                    onBtnClick2 = { //on done btn click
                        if(isEditable.value){
                            onDonePressed.value=true
                        } else {
                            navHostController.navigate(Destination.UserHome.routes)
                        } },
                    enable = isEditable.value
                )
            }
        }
    }
    if(isUpdating.value) showProgress()

}