package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.ImageWithCaptions

import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem
import java.util.Locale


var isPESetUpDone = false
var isFromPESave = false
var isPEDoneClick = false
@Composable
fun PhysicalExaminationScreen(navHostController: NavHostController){
    Disableback()

    val isEditable = MainActivity.subUserRepo.isEditTextEnable

    val isUpdating = remember { mutableStateOf(false) }

    var physicalExam = MainActivity.subUserRepo.isTempPopUpText

    var showPicUploadAlert = remember { mutableStateOf(false) }

    val onDonePressed= remember { mutableStateOf(false) }

    val selectedSession = MainActivity.sessionRepo.selectedsession

    val parsedText = selectedSession!!.PhysicalExamination.split("-:-")

    if(parsedText.size == 2 && !isPESetUpDone){
        physicalExam.value = parsedText.first()
        isPESetUpDone = true
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
            //MainActivity.subUserRepo.updateEditTextEnable(false)
            if(isFromPESave) MainActivity.subUserRepo.updateEditTextEnable(false)
            MainActivity.subUserRepo.updateIsAnyUpdateThere(false)
            if(isPEDoneClick) navHostController.navigate(Destination.UserHome.routes)

            //isEditable.value = false
            // refresh session list
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

    Column(Modifier.fillMaxSize()){
        Spacer(modifier = Modifier.height(15.dp))
        if(isFromVital){
            TopBarWithEditBtn(title = "Physical Examination")
        } else{
            TopBarWithBackEditBtn(onBackClick = {
                if(MainActivity.subUserRepo.anyUpdateThere.value) {
                    onDonePressed.value = true
                }
                else {
                    MainActivity.subUserRepo.updateEditTextEnable(false)
                    navHostController.navigate(Destination.UserHome.routes)
                } },
                title = "Physical Examination",
                onSaveClick = {
                    //on save click
                    isUpdating.value = true
                    isFromPESave = true
                    val text = physicalExam.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession.PhysicalExamination = "${text}-:-${newUpdatedList}"
                    MainActivity.sessionRepo.updateSession(selectedSession)
                })
        }
        Spacer(modifier = Modifier.height(40.dp))

        LazyColumn(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)){
            item {
                InputTextField(
                    textInput = physicalExam.value,
                    onChangeInput = { newValue ->
                        physicalExam.value = newValue
                        MainActivity.subUserRepo.updateTempPopUpText(physicalExam.value)
                        MainActivity.subUserRepo.updateIsAnyUpdateThere(true)
                    },
                    placeholder = "Head, eyes, chest, heart, lung, abdomen, extremities, skin, others",
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
                        MainActivity.cameraRepo.updateAttachmentScreenNo("PE")
                        navHostController.navigate(Destination.SavedImagePreviewScreen.routes)
                    }) { attachment ->
                        // Delete
                        val list = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().filter { it != attachment }
                        // update the list ->
                        isUpdating.value = true
                        val selectedSession = MainActivity.sessionRepo.selectedsession
                        val newList = list.toString()
                        selectedSession!!.PhysicalExamination = "${physicalExam.value}-:-$newList"
                        MainActivity.sessionRepo.clearImageList()
                        list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }
                        MainActivity.sessionRepo.updateSession(selectedSession)
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                PhotoBtn {
                    //on photoBtnClick
                    isFromPESave = false
                    MainActivity.cameraRepo.updateAttachmentScreenNo("PE")
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
                    val text = physicalExam.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession.PhysicalExamination = "${text}-:-${newUpdatedList}"
                    MainActivity.sessionRepo.clearImageList()
                    navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                }, modifier = Modifier.fillMaxWidth())
            }else{
                PopUpBtnSingle(btnName = "Done",
                    onBtnClick = { //on save click
                        isPEDoneClick = true
                    isUpdating.value = true
                    isFromPESave = true
                    val text = physicalExam.value
                    val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
                    selectedSession.PhysicalExamination = "${text}-:-${newUpdatedList}"
                    MainActivity.sessionRepo.updateSession(selectedSession) }, Modifier.fillMaxWidth())
//                PopBtnDouble(
//                    btnName1 = "Save",
//                    btnName2 = "Done",
//                    onBtnClick1 = {
//                        //on save click
//                        isUpdating.value = true
//                        isFromPESave = true
//                        val text = physicalExam.value
//                        val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()
//                        selectedSession.PhysicalExamination = "${text}-:-${newUpdatedList}"
//                        MainActivity.sessionRepo.updateSession(selectedSession)
//                    },
//                    onBtnClick2 = {
//                        //on done btn click
//                        if(isEditable.value){
//                            onDonePressed.value=true
//                        } else {
//                            navHostController.navigate(Destination.UserHome.routes)
//                        }
//                    },
//                    enable = isEditable.value
//                )
            }
        }
    }
    if(isUpdating.value) showProgress()
}



@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopBarWithEditBtn(title: String){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 15.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        BoldTextView(title = title, fontSize = 20)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopBarWithBackEditBtn(onBackClick: () -> Unit ,title: String, onSaveClick: () -> Unit){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(end = 15.dp), verticalAlignment = Alignment.CenterVertically) {

        IconButton(onClick = { onBackClick() }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "BackBtn")
        }

        BoldTextView(title = title, fontSize = 20)

        Box(modifier = Modifier
            .weight(1f), contentAlignment = Alignment.CenterEnd) {
            IconButton(
                onClick = {
                    onSaveClick()
//                    if(!isEditable.value)
//                        MainActivity.subUserRepo.updateEditTextEnable(true)
                },
                modifier = Modifier
                    .size(30.dp) // Adjust the size of the circular border
                    .border(
                        width = 2.dp, // Adjust the border width
                        color = Color.Black, // Change the border color when in edit mode
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.floppy_disk_icon),
                    contentDescription = "SaveBtn", Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTextField(
    textInput: String,
    onChangeInput: (String) -> Unit,
    placeholder: String,
    keyboard: KeyboardType,
    enable: Boolean = true,
    TestTag: String
) {

    val speechIntentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == ComponentActivity.RESULT_OK && result.data != null) {
            val resultData = result.data
            val resultText = resultData?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = resultText?.get(0)
            recognizedText?.let {
                onChangeInput("$textInput $it")
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(300.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        OutlinedTextField(
            value = textInput,
            onValueChange = { newValue -> onChangeInput(newValue) },
            placeholder = { RegularTextView(title = placeholder, fontSize = 16, textColor = Color.Gray) },
            modifier = Modifier
                .fillMaxSize()
                .testTag(TestTag),
            enabled = enable,
            textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.roboto_regular)), fontSize = 16.sp ),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color(0xffdae3f3))
        )

        IconButton(
            onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
                try {
                    speechIntentLauncher.launch(intent)
                } catch (e: Exception) {
                    // Handle exceptions as needed
                }
            },
            modifier = Modifier.padding(bottom = 4.dp),
            enabled = enable
        ) {
            Icon(imageVector = Icons.Default.Mic, contentDescription = "", modifier = Modifier.size(25.dp))
        }
    }
}


@Composable
fun PopUpBtnSingle(btnName: String, onBtnClick: () -> Unit, modifier: Modifier = Modifier){
    CustomBtnStyle(btnName = btnName, onBtnClick = { onBtnClick() }, textColor = Color.White, modifier = modifier)
}

@Composable
fun PopBtnDouble(btnName1: String, btnName2: String, onBtnClick1: () -> Unit, onBtnClick2: () -> Unit, enable: Boolean = true){
    Row( modifier = Modifier
        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        CustomBtnStyle(btnName = btnName1, onBtnClick = { onBtnClick1() }, textColor = if(enable) Color.White else Color.Black, enabled = enable)
        CustomBtnStyle(btnName = btnName2, onBtnClick = { onBtnClick2() }, textColor = Color.White)
    }
}


@Composable
fun AttachmentRow(attachment : ImageWithCaptions ,btnName: String, onBtnClick: () -> Unit, onDeleteClick: (ImageWithCaptions) -> Unit ){
Row(
    Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
    Button(onClick = {onBtnClick()},
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent),
        modifier = Modifier.width(250.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = btnName,
                fontFamily = FontFamily(Font(R.font.roboto_regular)),
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1, // Set the maximum number of lines
                overflow = TextOverflow.Ellipsis,
            )
            //RegularTextView(title = btnName, 16)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    IconButton(onClick = {
        onDeleteClick(attachment) }) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = "DeleteIcon")
    }
}
}

@Composable
fun PhotoBtn(onUploadClick: () -> Unit){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)){
        Button(onClick = {onUploadClick()},
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2f5597)),
            modifier = Modifier.width(250.dp)
        ) {
            RegularTextView(title = "Click to add a new picture", textColor = Color.White, fontSize = 16)
        }
    }
}



@Composable
fun ImagePickerDialog(
    onCancelClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    AlertDialog(onDismissRequest = { /*TODO*/ },
        confirmButton = { },
        title = {
                TitleViewWithCancelBtn(title = "Choose an option") {
                    onCancelClick()
                }
        },
        text = {
            Row() {
                Button(onClick = { onGalleryClick() }) {
                    Icon(imageVector = Icons.Default.BrowseGallery, contentDescription = "GalleryIcon")
                }
                Button(onClick = { onCameraClick() }) {
                    Icon(imageVector = Icons.Default.Camera, contentDescription = "CameraIcon")
                }
            }

        }

    )
}




@Preview
@Composable
fun previe(){
    PhysicalExaminationScreen(navHostController = rememberNavController())
}
