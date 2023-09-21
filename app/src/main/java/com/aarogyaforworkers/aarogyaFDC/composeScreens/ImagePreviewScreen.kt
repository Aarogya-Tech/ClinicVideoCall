package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropRotate
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Camera.CameraRepository
import com.aarogyaforworkers.aarogyaFDC.Commons.bitmapToByteArray
import com.aarogyaforworkers.aarogyaFDC.Commons.selectedSession
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentRowItem
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.ImageWithCaptions
import com.github.mikephil.charting.utils.Utils.drawImage
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.CropperStyle
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.rememberDrawController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(cameraRepository: CameraRepository, navHostController: NavHostController) {

    Disableback()

    var isLoading = remember { mutableStateOf(false) }


    var onBackSI= remember {
        mutableStateOf(false)
    }

    var capturedImageBitmap = cameraRepository.capturedImageBitmap
    var caption = remember { mutableStateOf("") }
    if(isfromSavedImage==1)
    {
//        if(cameraRepository.selectedPreviewImage.value == null)
//        {
//            LoadImageFromUrl(MainActivity.cameraRepo.savedImageView.value!!.imageLink)
//            capturedImageBitmap=cameraRepository.capturedImageBitmap
//        }
        caption.value=MainActivity.cameraRepo.savedImageView.value!!.caption
        isfromSavedImage=2
    }

    val isUploading = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val selectedUser = MainActivity.adminDBRepo.getSelectedSubUserProfile().copy()

    var selectedSession_ = MainActivity.sessionRepo.selectedsession

    when (MainActivity.sessionRepo.sessionUpdatedStatus.value) {

        true -> {

            isUploading.value = false
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
            // refresh session list
            if(isfromSavedImage!=2 || isfromSavedImage!=0)
            {
                when (MainActivity.cameraRepo.isAttachmentScreen.value) {

                    "PE" -> {
                        navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                    }

                    "LR" -> {
                        navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                    }

                    "IP" -> {
                        navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                    }

                    "PMSH" ->{
                        navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
                    }
                }
            }
        }

        false -> {
            isUploading.value = false
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
        }

        null -> {

        }

    }

    when (MainActivity.sessionRepo.attachmentUploadedStatus.value) {

        true -> {
            // image is saved successfully now update session
            val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()

            when (MainActivity.cameraRepo.isAttachmentScreen.value) {

                "PE" -> {
                    val title = selectedSession_!!.PhysicalExamination.split("-:-")
                    selectedSession_.PhysicalExamination = "${title.first()}-:-${newUpdatedList}"
                    if (isFromVital) {
                        navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                    } else {
                        MainActivity.sessionRepo.updateSession(selectedSession_)
                    }
                }

                "LR" -> {
                    val title = selectedSession_!!.LabotryRadiology.split("-:-")
                    selectedSession_.LabotryRadiology = "${title.first()}-:-${newUpdatedList}"
                    if (isFromVital) {
                        navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                    } else {
                        MainActivity.sessionRepo.updateSession(selectedSession_)
                    }
                }

                "IP" -> {
                    val title = selectedSession_!!.ImpressionPlan.split("-:-")
                    selectedSession_.ImpressionPlan = "${title.first()}-:-${newUpdatedList}"
                    if (isFromVital) {
                        navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                    } else {
                        MainActivity.sessionRepo.updateSession(selectedSession_)
                    }
                }
                "PMSH" -> {
                    val title = selectedUser.PastMedicalSurgicalHistory.split("-:-")
                    selectedUser.PastMedicalSurgicalHistory = "${title.first()}-:-${newUpdatedList}"
                    navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
                    MainActivity.adminDBRepo.adminUpdateSubUser(user = selectedUser)
                    MainActivity.adminDBRepo.setNewSubUserprofile(selectedUser.copy())
                    MainActivity.adminDBRepo.setNewSubUserprofileCopy(selectedUser.copy())

                }
            }

            MainActivity.sessionRepo.updateAttachmentUploadedStatus(null)

            if(isfromSavedImage==3)
            {
                val AttachmentPreviewItem=cameraRepository.savedImageView.value
                val attachment= ImageWithCaptions(caption = AttachmentPreviewItem!!.caption, imageLink = AttachmentPreviewItem!!.imageLink)
                val list = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().filter { it != attachment }
                val selectedSession = MainActivity.sessionRepo.selectedsession
                val newList = list.toString()
                when(MainActivity.cameraRepo.isAttachmentScreen.value){
                    "PE" -> {
                        val title = selectedSession!!.PhysicalExamination.split("-:-")
                        selectedSession.PhysicalExamination = "${title.first()}-:-${newList}"
                        if (isFromVital) {
                            navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                        } else {
                            MainActivity.sessionRepo.updateSession(selectedSession)
                            MainActivity.sessionRepo.clearImageList()
                            list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }
                        }
                    }
                    "LR" -> {
                        val title = selectedSession_!!.LabotryRadiology.split("-:-")
                        selectedSession_.LabotryRadiology = "${title.first()}-:-${newUpdatedList}"
                        if (isFromVital) {
                            navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                        } else {
                            MainActivity.sessionRepo.updateSession(selectedSession_)
                            MainActivity.sessionRepo.clearImageList()
                            list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }
                        }
                    }

                    "IP" -> {
                        val title = selectedSession_!!.ImpressionPlan.split("-:-")
                        selectedSession_.ImpressionPlan = "${title.first()}-:-${newUpdatedList}"
                        if (isFromVital) {
                            navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                        } else {
                            MainActivity.sessionRepo.updateSession(selectedSession_)
                            MainActivity.sessionRepo.clearImageList()
                            list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }
                        }
                    }
                    "PMSH" -> {
                        val title = selectedUser.PastMedicalSurgicalHistory.split("-:-")
                        selectedUser.PastMedicalSurgicalHistory = "${title.first()}-:-${newUpdatedList}"
                        navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
                        MainActivity.adminDBRepo.adminUpdateSubUser(user = selectedUser)
                        MainActivity.adminDBRepo.setNewSubUserprofile(selectedUser.copy())
                        MainActivity.adminDBRepo.setNewSubUserprofileCopy(selectedUser.copy())
                        MainActivity.sessionRepo.clearImageList()
                        list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }
                    }
                }
            }
        }

        false -> {
            isUploading.value = false
            MainActivity.sessionRepo.updateAttachmentUploadedStatus(null)
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
        }

        null -> {

        }

    }

    val isCropRotate = remember {
        mutableStateOf(false)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    if (isCropRotate.value) {
        CropAndRotate(
            cameraRepository = cameraRepository,
            capturedImageBitmap = capturedImageBitmap,
            onBackClick = {
                isCropRotate.value = false
            })
    }
    if (capturedImageBitmap.value != null) {
        Box(modifier = Modifier.fillMaxSize()) {
//             The Image is the background of the Box, filling the whole size
            Image(
                bitmap = capturedImageBitmap.value!!.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                containerColor = Color.Transparent,
                topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                        title = {
                            Text(
                                "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    onBackSI.value=true
                                },
                                modifier = Modifier
                                    .then(
                                        Modifier
                                            .size(48.dp)
                                            .background(
                                                color = Color.LightGray,
                                                shape = CircleShape
                                            )
                                    )
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Localized description",
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = {
                                    isCropRotate.value = true
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(color = Color.LightGray, shape = CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CropRotate,
                                    contentDescription = "Localized description",
                                    tint = Color.White,
                                )
                            }

//                            Spacer(modifier = Modifier.width(4.dp))

//                            IconButton(
//                                onClick = {
//                                },
//                                modifier = Modifier
//                                    .size(48.dp)
//                                    .background(color = Color.LightGray, shape = CircleShape)
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.TextFields,
//                                    contentDescription = "Localized description",
//                                    tint = Color.White,
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(4.dp))
//
//                            IconButton(
//                                onClick = {
//                                          navHostController.navigate(Destination.ImagePainter.routes)
//                                },
//                                modifier = Modifier
//                                    .size(48.dp)
//                                    .background(color = Color.LightGray, shape = CircleShape)
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Edit,
//                                    contentDescription = "Localized description",
//                                    tint = Color.White,
//                                )
//                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
            ) { innerPadding ->
                if(onBackSI.value)
                {
                    if(isfromSavedImage==2){
                        AlertView(
                            showAlert = true,
                            title = "Do you want to go back?",
                            subTitle = "You have unsaved changes.Your changes will be discarded if you press Yes.",
                            subTitle1 = "",
                            onYesClick = {
                                            when (MainActivity.cameraRepo.isAttachmentScreen.value) {

                                                "PE" -> {
                                                    navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                                                }

                                                "LR" -> {
                                                    navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                                                }

                                                "IP" -> {
                                                    navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                                                }

                                                "PMSH" ->{
                                                    navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
                                                }
                                            }
                                         },
                            onNoClick = { onBackSI.value=false },
                        ) {
                        }
                    }
                    else
                        navHostController.navigate(Destination.Camera.routes)
                }
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    Box(modifier = Modifier.fillMaxSize()) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                        ) {

                            TextField(
                                value = caption.value,
                                onValueChange = { newValue ->
                                    caption.value = newValue
                                },
                                placeholder = {
                                    RegularTextView(
                                        "Add caption...",
                                        16,
                                        textColor = Color.Gray
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                enabled = true,
                                textStyle = TextStyle(
                                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                                    fontSize = 16.sp
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(5.dp)
                            )

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp, vertical = 16.dp)
                            ) {
                                PopBtnDouble(
                                    btnName1 = "Save",
                                    btnName2 = "Cancel",
                                    onBtnClick1 = {
                                        //on save btn click

                                        if(isfromSavedImage==2){
                                            isfromSavedImage=3
                                        }

                                        isUploading.value = true

                                        val imageNo =
                                            MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().size + 1

                                        when (MainActivity.cameraRepo.isAttachmentScreen.value) {
                                            "PE" -> {
                                                caption.value =
                                                    caption.value.ifEmpty { "Physical Examination $imageNo" }

                                                thread {
                                                    val image = bitmapToByteArray(
                                                        capturedImageBitmap.value!!.asImageBitmap()
                                                            .asAndroidBitmap()
                                                    )
                                                    val randomUUId =
                                                        selectedSession.userId.take(6) + UUID.randomUUID()
                                                            .toString().takeLast(6)
                                                    // Perform the upload operation here
                                                    MainActivity.s3Repo.startUploadingAttachments(
                                                        image,
                                                        randomUUId,
                                                        caption.value,
                                                        0
                                                    )
                                                }
                                                MainActivity.cameraRepo.updatePEImageList(
                                                    AttachmentRowItem(
                                                        caption.value,
                                                        capturedImageBitmap.value!!.asImageBitmap(),
                                                        false
                                                    )
                                                )
                                            }

                                            "LR" -> {

                                                caption.value =
                                                    caption.value.ifEmpty { "Laboratory & Radiology $imageNo" }

                                                thread {
                                                    val image = bitmapToByteArray(
                                                        capturedImageBitmap.value!!.asImageBitmap()
                                                            .asAndroidBitmap()
                                                    )
                                                    val randomUUId =
                                                        selectedSession.userId.take(6) + UUID.randomUUID()
                                                            .toString().takeLast(6)
                                                    // Perform the upload operation here
                                                    MainActivity.s3Repo.startUploadingAttachments(
                                                        image,
                                                        randomUUId,
                                                        caption.value,
                                                        0
                                                    )
                                                }
                                                MainActivity.cameraRepo.updateLRImageList(
                                                    AttachmentRowItem(
                                                        caption.value,
                                                        capturedImageBitmap.value!!.asImageBitmap(),
                                                        false
                                                    )
                                                )
                                            }

                                            "IP" -> {

                                                caption.value =
                                                    caption.value.ifEmpty { "Impression & Plan $imageNo" }

                                                thread {
                                                    val image = bitmapToByteArray(
                                                        capturedImageBitmap.value!!.asImageBitmap()
                                                            .asAndroidBitmap()
                                                    )
                                                    val randomUUId =
                                                        selectedSession.userId.take(6) + UUID.randomUUID()
                                                            .toString().takeLast(6)
                                                    // Perform the upload operation here
                                                    MainActivity.s3Repo.startUploadingAttachments(
                                                        image,
                                                        randomUUId,
                                                        caption.value,
                                                        0
                                                    )
                                                }
                                                MainActivity.cameraRepo.updateIPImageList(
                                                    AttachmentRowItem(
                                                        caption.value,
                                                        capturedImageBitmap.value!!.asImageBitmap(),
                                                        false
                                                    )
                                                )
                                            }

                                            "PMSH" -> {
                                                caption.value =
                                                    caption.value.ifEmpty { "Medical & Surgical $imageNo" }

                                                thread {
                                                    val image = bitmapToByteArray(
                                                        capturedImageBitmap.value!!.asImageBitmap()
                                                            .asAndroidBitmap()
                                                    )
                                                    val randomUUId =
                                                        selectedSession.userId.take(6) + UUID.randomUUID()
                                                            .toString().takeLast(6)
                                                    // Perform the upload operation here
                                                    MainActivity.s3Repo.startUploadingAttachments(
                                                        image,
                                                        randomUUId,
                                                        caption.value,
                                                        0
                                                    )
                                                }
                                                MainActivity.cameraRepo.updatePMSHImageList(
                                                    AttachmentRowItem(
                                                        caption.value,
                                                        capturedImageBitmap.value!!.asImageBitmap(),
                                                        false
                                                    )
                                                )
                                            }
                                        }
                                    },
                                    onBtnClick2 = {
                                        //on cancel btn click
                                        onBackSI.value=true
                                    })
                            }
                        }
                    }
                }
            }
        }
        if (isUploading.value || isLoading.value) showProgress()
    }
}

@Composable
fun CustomBtnStyle(btnName: String, onBtnClick: () -> Unit, enabled: Boolean = true, modifier: Modifier = Modifier, textColor: Color, containerColor: Color, disabledContainerColor: Color){
    Button(onClick = { onBtnClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = disabledContainerColor,
            containerColor = containerColor),
        enabled = enabled,
        modifier = modifier,
    ) {
        BoldTextView(title = btnName, fontSize = 18, textColor = textColor)
    }
}

@Composable
fun CropAndRotate(cameraRepository: CameraRepository,capturedImageBitmap: State<Bitmap?>,onBackClick:()->Unit)
{
    val scope = rememberCoroutineScope()
    val imageCropper = rememberImageCropper()
    LaunchedEffect(Unit)
    {
        scope.launch {
            val result = imageCropper.crop(bmp = capturedImageBitmap.value!!.asImageBitmap()) // Suspends until user accepts or cancels cropping
            when (result) {
                CropResult.Cancelled -> {
                    onBackClick()
                }
                is CropError -> {
                    onBackClick()
                }
                is CropResult.Success -> {
                    result.bitmap
                    cameraRepository.updateCapturedImage(result.bitmap.asAndroidBitmap())
                    onBackClick()
                }
            }
        }
    }
    val cropState = imageCropper.cropState
    if(cropState != null)
        ImageCropperDialog(
            state = cropState,
            dialogPadding= PaddingValues(0.dp),
        )
}

