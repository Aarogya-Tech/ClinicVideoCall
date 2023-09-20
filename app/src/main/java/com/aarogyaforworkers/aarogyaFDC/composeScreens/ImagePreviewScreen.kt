package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
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
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Camera.CameraRepository
import com.aarogyaforworkers.aarogyaFDC.Commons.bitmapToByteArray
import com.aarogyaforworkers.aarogyaFDC.Commons.selectedSession
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentRowItem
import com.github.mikephil.charting.utils.Utils.drawImage
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.CropperStyle
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(cameraRepository: CameraRepository, navHostController: NavHostController) {

    Disableback()

    val capturedImageBitmap = cameraRepository.capturedImageBitmap
    val caption = remember { mutableStateOf("") }
    val isUploading = remember { mutableStateOf(false) }
    val context = LocalContext.current

    var selectedSession_ = MainActivity.sessionRepo.selectedsession

    when(MainActivity.sessionRepo.sessionUpdatedStatus.value){

        true -> {

            isUploading.value = false
            MainActivity.sessionRepo.updateIsSessionUpdatedStatus(null)
            // refresh session list
            when(MainActivity.cameraRepo.isAttachmentScreen.value){

                "PE" -> {
                    navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                }

                "LR" -> {
                    navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                }

                "IP" -> {
                    navHostController.navigate(Destination.ImpressionPlanScreen.routes)
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

    when(MainActivity.sessionRepo.attachmentUploadedStatus.value){

        true -> {
            // image is saved successfully now update session

            val newUpdatedList = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().toString()

            when(MainActivity.cameraRepo.isAttachmentScreen.value){

                "PE" -> {
                    val title = selectedSession_!!.PhysicalExamination.split("-:-")
                    selectedSession_.PhysicalExamination = "${title.first()}-:-${newUpdatedList}"
                    if(isFromVital){
                        navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                    }else{
                        MainActivity.sessionRepo.updateSession(selectedSession_)
                    }
                }

                "LR" -> {
                    val title = selectedSession_!!.LabotryRadiology.split("-:-")
                    selectedSession_.LabotryRadiology = "${title.first()}-:-${newUpdatedList}"
                    if(isFromVital){
                        navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                    }else{
                        MainActivity.sessionRepo.updateSession(selectedSession_)
                    }
                }

                "IP" -> {
                    val title = selectedSession_!!.ImpressionPlan.split("-:-")
                    selectedSession_.ImpressionPlan = "${title.first()}-:-${newUpdatedList}"
                    if(isFromVital){
                        navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                    }else{
                        MainActivity.sessionRepo.updateSession(selectedSession_)
                    }
                }
            }

            MainActivity.sessionRepo.updateAttachmentUploadedStatus(null)
        }

        false -> {
            isUploading.value = false
            MainActivity.sessionRepo.updateAttachmentUploadedStatus(null)
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
        }

        null -> {

        }

    }

    val isCropRotate= remember{
        mutableStateOf(false)
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    if(isCropRotate.value)
    {
        CropAndRotate(cameraRepository = cameraRepository, capturedImageBitmap = capturedImageBitmap,onBackClick={
            isCropRotate.value=false
        })
    }
    Box{
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
                        IconButton(onClick = {
                            navHostController.navigate(Destination.Camera.routes)
                                             },
                            modifier = Modifier
                                .then(Modifier.size(48.dp).background(color = Color.LightGray, shape = CircleShape))
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

                        IconButton(onClick = {
                            isCropRotate.value=true
                        },
                            modifier = Modifier.size(48.dp).background(color = Color.LightGray, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropRotate,
                                contentDescription = "Localized description",
                                tint = Color.White,
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(onClick = {
                        },
                            modifier = Modifier.size(48.dp).background(color = Color.LightGray, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = "Localized description",
                                tint = Color.White,
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(onClick = {
                        },
                            modifier = Modifier.size(48.dp).background(color = Color.LightGray, shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Localized description",
                                tint = Color.White,
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {

                Box(modifier = Modifier.fillMaxSize()) {

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)) {

                        TextField(
                            value = caption.value,
                            onValueChange = { newValue ->
                                caption.value = newValue
                            },
                            placeholder = { RegularTextView("Add caption...", 16) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            enabled = true,
                            textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.roboto_regular)), fontSize = 16.sp ),
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp)
                        )

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp, vertical = 16.dp)) {
                            PopBtnDouble(
                                btnName1 = "Save",
                                btnName2 = "Cancel",
                                onBtnClick1 = {
                                    //on save btn click
                                    isUploading.value = true

                                    when(MainActivity.cameraRepo.isAttachmentScreen.value){
                                        "PE" -> {
                                            thread {
                                                Log.d("TAG", "ImagePreviewScreen: clicked to upload")
                                                val image = bitmapToByteArray(capturedImageBitmap.value!!.asImageBitmap().asAndroidBitmap())
                                                val randomUUId = selectedSession.userId.take(6) + UUID.randomUUID().toString().takeLast(6)
                                                // Perform the upload operation here
                                                Log.d("TAG", "ImagePreviewScreen: staring to upload")
                                                MainActivity.s3Repo.startUploadingAttachments(image, randomUUId, caption.value, 0)
                                            }
                                            MainActivity.cameraRepo.updatePEImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(),false))
                                        }

                                        "LR" -> {
                                            thread {
                                                val image = bitmapToByteArray(capturedImageBitmap.value!!.asImageBitmap().asAndroidBitmap())
                                                val randomUUId = selectedSession.userId.take(6) + UUID.randomUUID().toString().takeLast(6)
                                                // Perform the upload operation here
                                                MainActivity.s3Repo.startUploadingAttachments(image, randomUUId, caption.value, 0)
                                            }
                                            MainActivity.cameraRepo.updateLRImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(), false))
                                        }
                                        "IP" -> {
                                            thread {
                                                val image = bitmapToByteArray(capturedImageBitmap.value!!.asImageBitmap().asAndroidBitmap())
                                                val randomUUId = selectedSession.userId.take(6) + UUID.randomUUID().toString().takeLast(6)
                                                // Perform the upload operation here
                                                MainActivity.s3Repo.startUploadingAttachments(image, randomUUId, caption.value, 0)
                                            }
                                            MainActivity.cameraRepo.updateIPImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(), false))
                                        }
                                    }
                                },
                                onBtnClick2 = {
                                    //on cancel btn click
                                    navHostController.navigate(Destination.Camera.routes) })
                        }
                    }
                }
            }
        }
    }
    if(isUploading.value) showProgress()
}


@Composable
fun CustomBtnStyle(btnName: String, onBtnClick: () -> Unit, enabled: Boolean = true, modifier: Modifier = Modifier, textColor: Color){
    Button(onClick = { onBtnClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color(0xffdae3f3),
            containerColor = Color(0xFF2f5597)),
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
