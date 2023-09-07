package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogyaFDC.Camera.CameraRepository
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentRowItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(cameraRepository: CameraRepository, navHostController: NavHostController) {
    val capturedImageBitmap = cameraRepository.capturedImageBitmap// Assuming you've stored the bitmap in the repo.
    var caption = remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // The Image is the background of the Box, filling the whole size
        Image(
            bitmap = capturedImageBitmap.value!!.asImageBitmap(),
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)) {
            TextField(
                value = caption.value,
                onValueChange = { newValue ->
                    caption.value = newValue.take(10)
                },
                placeholder = { RegularTextView("Add caption...", 16) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                enabled = true,
                textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.roboto_regular)), fontSize = 16.sp ),
                singleLine = true,
                shape = RoundedCornerShape(5.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                //onCancel btn click
                CustomBtnStyle(btnName = "Cancel", onBtnClick = { navHostController.navigate(Destination.Camera.routes) }, textColor = Color.White)

                //onSave btn Click
                CustomBtnStyle(btnName = "Save", onBtnClick = {
                    when(MainActivity.cameraRepo.isAttachmentScreen.value){
                        "PE" -> {
                            MainActivity.cameraRepo.updatePEImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(),false))
                            navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                        }
                        "LR" -> {
                            MainActivity.cameraRepo.updateLRImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(), false))
                            navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                        }
                        "IP" -> {
                            MainActivity.cameraRepo.updateIPImageList(AttachmentRowItem(caption.value, capturedImageBitmap.value!!.asImageBitmap(), false))
                            navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                        }
                    }
                }, textColor = Color.White)
            }
        }
    }
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
