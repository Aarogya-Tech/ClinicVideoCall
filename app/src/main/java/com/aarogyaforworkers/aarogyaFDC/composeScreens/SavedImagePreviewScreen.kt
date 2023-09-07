package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.media.Image
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.aarogyaforworkers.aarogyaFDC.Camera.CameraRepository
import com.aarogyaforworkers.aarogyaFDC.Commons.isSaving
import com.aarogyaforworkers.aarogyaFDC.Commons.timestamp
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.ui.theme.defDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SavedImagePreviewScreen(
    navHostController: NavHostController,
    cameraRepository: CameraRepository,
) {
    val context = LocalContext.current // Required for displaying a toast
    var isLoading = remember { mutableStateOf(false) }

    val profileUrlWithTimestamp = "${MainActivity.cameraRepo.savedImageView.value!!.imageLink}?t=$timestamp"
    val painter = rememberImagePainter(data = profileUrlWithTimestamp)
    val coroutineScope = rememberCoroutineScope()

//    LaunchedEffect(painter) {
//        if (painter.state is ImagePainter.State.Loading) {
//            coroutineScope.launch {
//                while (painter.state is ImagePainter.State.Loading) {
//                }
//            }
//        }
//    }

    when (painter.state) {
        is ImagePainter.State.Loading -> isLoading.value = true
        else -> isLoading.value = false
    }

        Box(Modifier.fillMaxSize()) {
            Image(
                painter = painter,
                contentDescription = "Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = .7f))
            ) {
                IconButton(onClick = {
                    when(MainActivity.cameraRepo.isAttachmentScreen.value){
                        "PE" -> navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                        "LR" -> navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                        "IP" -> navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "backIcon", tint = Color.Black)
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomStart)
                    .background(Color.White.copy(alpha = .5f)) ) {
                RegularTextView(title = MainActivity.cameraRepo.savedImageView.value!!.caption, fontSize = 18, modifier = Modifier.padding(16.dp), textColor = Color.Black)
            }
        }
    if(isLoading.value) showProgress()
}
