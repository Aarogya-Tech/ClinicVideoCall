package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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

                "PMSH" ->{
                    navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
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
                "PMSH" -> {
                    val title = MainActivity.adminDBRepo.getSelectedSubUserProfile().PastMedicalSurgicalHistory.split("-:-")
                    MainActivity.adminDBRepo.getSelectedSubUserProfile().PastMedicalSurgicalHistory = "${title.first()}-:-${newUpdatedList}"
                    navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
//                    if(isFromVital){
//                        navHostController.navigate(Destination.ImpressionPlanScreen.routes)
//                    }else{
//                        MainActivity.sessionRepo.updateSession(selectedSession_)
//                    }
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



    if(capturedImageBitmap.value != null) {
        Column(Modifier.fillMaxSize()) {

            Box(modifier = Modifier.fillMaxSize()) {
                // The Image is the background of the Box, filling the whole size
                Image(
                    bitmap = capturedImageBitmap.value!!.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)) {

                    TextField(
                        value = caption.value,
                        onValueChange = { newValue ->
                            caption.value = newValue
                        },
                    placeholder = { RegularTextView("Add caption...", 16, textColor = Color.Gray) },
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

                            val imageNo = MainActivity.sessionRepo.imageWithCaptionsList.value.size + 1

                                when(MainActivity.cameraRepo.isAttachmentScreen.value){
                                    "PE" -> {
                                        caption.value = caption.value.ifEmpty { "Physical Examination $imageNo" }

                                        thread {
                                            val image = bitmapToByteArray(capturedImageBitmap.value!!.asImageBitmap().asAndroidBitmap())
                                            val randomUUId = selectedSession.userId.take(6) + UUID.randomUUID().toString().takeLast(6)
                                            // Perform the upload operation here
                                            MainActivity.s3Repo.startUploadingAttachments(image, randomUUId, caption.value, 0)
                                        }
                                        MainActivity.cameraRepo.updatePEImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(),false))
                                    }

                                    "LR" -> {

                                    caption.value = caption.value.ifEmpty { "Laboratory & Radiology $imageNo" }

                                        thread {
                                            val image = bitmapToByteArray(capturedImageBitmap.value!!.asImageBitmap().asAndroidBitmap())
                                            val randomUUId = selectedSession.userId.take(6) + UUID.randomUUID().toString().takeLast(6)
                                            // Perform the upload operation here
                                            MainActivity.s3Repo.startUploadingAttachments(image, randomUUId, caption.value, 0)
                                        }
                                        MainActivity.cameraRepo.updateLRImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(), false))
                                    }
                                    "IP" -> {

                                    caption.value = caption.value.ifEmpty { "Impression & Plan $imageNo" }

                                        thread {
                                            val image = bitmapToByteArray(capturedImageBitmap.value!!.asImageBitmap().asAndroidBitmap())
                                            val randomUUId = selectedSession.userId.take(6) + UUID.randomUUID().toString().takeLast(6)
                                            // Perform the upload operation here
                                            MainActivity.s3Repo.startUploadingAttachments(image, randomUUId, caption.value, 0)
                                        }
                                        MainActivity.cameraRepo.updateIPImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(), false))
                                    }

                                "PMSH" ->{
                                    caption.value = caption.value.ifEmpty { "Medical & Surgical $imageNo" }

                                    thread {
                                        val image = bitmapToByteArray(capturedImageBitmap.value!!.asImageBitmap().asAndroidBitmap())
                                        val randomUUId = selectedSession.userId.take(6) + UUID.randomUUID().toString().takeLast(6)
                                        // Perform the upload operation here
                                        MainActivity.s3Repo.startUploadingAttachments(image, randomUUId, caption.value, 0)
                                    }
                                    MainActivity.cameraRepo.updatePMSHImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(), false))
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

    if(isUploading.value) showProgress()
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
