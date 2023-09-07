package com.aarogyaforworkers.aarogyaFDC.composeScreens

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentRowItem

@Composable
fun LaboratoryRadioLogyScreen(navHostController: NavHostController){

    var isUpdating = remember { mutableStateOf(false) }

    var isEditable = remember { mutableStateOf(false) }

    var labRadio = remember { mutableStateOf("") }

    var showPicUploadAlert = remember { mutableStateOf(false) }

    var selectedSession = MainActivity.sessionRepo.selectedsession

    if(isFromVital) isEditable.value = true

    val parsedText = selectedSession!!.LabotryRadiology.split("-:-")

    labRadio.value = parsedText.first()

    if(parsedText.size == 2){
        val listIOfImages = MainActivity.sessionRepo.parseImageList(parsedText[1])
        listIOfImages.forEach {
            MainActivity.sessionRepo.updateImageWithCaptionList(it)
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
            isEditable.value = false
            // refresh session list
        }

        false -> {

            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)

        }

        null -> {

        }

    }


    Column(Modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 15.dp, top = 40.dp, bottom = 20.dp)) {
        if(isFromVital){
            TopBarWithEditBtn(title = "Laboratory & Radiology")
        } else{
            TopBarWithBackEditBtn(onBackClick = { navHostController.navigate(Destination.UserHome.routes) }, title = "Laboratory & Radiology", isEditable = isEditable)
        }

        Spacer(modifier = Modifier.height(40.dp))

        LazyColumn{
            item {
                InputTextField(
                    textInput = labRadio.value,
                    onChangeInput = { newValue ->
                        labRadio.value = newValue
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
                        MainActivity.cameraRepo.updateAttachmentScreenNo("LR")
                        navHostController.navigate(Destination.SavedImagePreviewScreen.routes)
                    }) {

                    }
                }

                Spacer(modifier = Modifier.height(15.dp))



                PhotoBtn {
                    //on photoBtnClick
                    MainActivity.cameraRepo.updateAttachmentScreenNo("LR")
                    navHostController.navigate(Destination.Camera.routes)
                }

            }
        }
        Row(
            Modifier
                .fillMaxWidth().padding(16.dp)
                .weight(1f), verticalAlignment = Alignment.Bottom) {
            if (isFromVital){
                PopUpBtnSingle(btnName = "Next") {
                    val text = labRadio.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession.LabotryRadiology = "${text}-:-${newUpdatedList}"
                    navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                }
            }else{
                PopBtnDouble(btnName1 = "Save", btnName2 = "Done", onBtnClick1 = {
                    //on save btn click
                    val text = labRadio.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession.LabotryRadiology = "${text}-:-${newUpdatedList}"
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