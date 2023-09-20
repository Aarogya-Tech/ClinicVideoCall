package com.aarogyaforworkers.aarogyaFDC.composeScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.composeScreens.isFromVital
import com.aarogyaforworkers.aarogyaFDC.Camera.CameraRepository
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.ImageWithCaptions


var isfromSavedImage=0
@Composable
fun SavedImagePreviewScreen2(navHostController: NavHostController, cameraRepository: CameraRepository) {

    if(cameraRepository.selectedPreviewImage.value != null){

        Box(Modifier.fillMaxSize()) {

            Image(
                bitmap = cameraRepository.selectedPreviewImage.value!!.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = .7f)),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    when(MainActivity.cameraRepo.isAttachmentScreen.value){
                        "PE" -> navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                        "LR" -> navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                        "IP" -> navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                        "PMSH" -> navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "backIcon", tint = Color.Black)
                }


                var physicalExam = MainActivity.subUserRepo.isTempPopUpText
                val AttachmentPreviewItem=cameraRepository.savedImageView.value
                val attachment=ImageWithCaptions(caption = AttachmentPreviewItem!!.caption, imageLink = AttachmentPreviewItem!!.imageLink)
                IconButton(onClick = {
                    cameraRepository.updateCapturedImage(cameraRepository.selectedPreviewImage.value)

                    val list = MainActivity.sessionRepo.imageWithCaptionsList.value.filterNotNull().filter { it != attachment }
                    // update the list ->
                    val selectedSession = MainActivity.sessionRepo.selectedsession
                    val newList = list.toString()
                    when(MainActivity.cameraRepo.isAttachmentScreen.value){
                        "PE" -> {
                            val title = selectedSession!!.PhysicalExamination.split("-:-")
                            selectedSession.PhysicalExamination = "${title.first()}-:-${newList}"
                            if (isFromVital) {
//                                navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                            } else {
                                MainActivity.sessionRepo.updateSession(selectedSession)
                                MainActivity.sessionRepo.clearImageList()
                                list.forEach { MainActivity.sessionRepo.updateImageWithCaptionList(it) }
                            }
                        }

//                            selectedSession?.PhysicalExamination = "${physicalExam.value}-:-$newList"
                        "LR" -> navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                        "IP" -> navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                        "PMSH" -> navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
                    }
//                    MainActivity.sessionRepo.updateSession(selectedSession)


                    isfromSavedImage=1
                    navHostController.navigate(Destination.ImagePreviewScreen.routes)
                } ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "EditIcon", tint = Color.Black)
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .align(Alignment.BottomStart)
                    .background(Color.White.copy(alpha = .5f)) ) {
                Text(
                    text = MainActivity.cameraRepo.savedImageView.value!!.caption,
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2, // Set the maximum number of lines
                    overflow = TextOverflow.Ellipsis ,
                    modifier= Modifier.padding(16.dp)
                )
            }
        }

    }else{

        var isLoading = remember { mutableStateOf(false) }

        val profileUrlWithTimestamp = MainActivity.cameraRepo.savedImageView.value!!.imageLink

        val painter = rememberImagePainter(data = profileUrlWithTimestamp)

        when (painter.state) {
            is ImagePainter.State.Loading -> isLoading.value = true
            else -> isLoading.value = false
        }

        Box(Modifier.fillMaxSize()) {

            Image(  painter = painter,
                contentDescription = "Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = .7f)),
                horizontalArrangement=Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    when(MainActivity.cameraRepo.isAttachmentScreen.value){
                        "PE" -> navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                        "LR" -> navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                        "IP" -> navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                        "PMSH" -> navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "backIcon", tint = Color.Black)
                }
                IconButton(onClick = {
                    cameraRepository.updateCapturedImage(cameraRepository.selectedPreviewImage.value)
                    navHostController.navigate(Destination.ImagePreviewScreen.routes)
                } ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "EditIcon", tint = Color.Black)
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .align(Alignment.BottomStart)
                    .background(Color.White.copy(alpha = .5f)) ) {
                Text(
                    text = MainActivity.cameraRepo.savedImageView.value!!.caption,
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2, // Set the maximum number of lines
                    overflow = TextOverflow.Ellipsis ,
                    modifier=Modifier.padding(16.dp)
                )
                //RegularTextView(title = MainActivity.cameraRepo.savedImageView.value!!.caption, fontSize = 16, modifier = Modifier.padding(16.dp), textColor = Color.Black)
            }
        }

        if(isLoading.value) showProgress()
    }

}