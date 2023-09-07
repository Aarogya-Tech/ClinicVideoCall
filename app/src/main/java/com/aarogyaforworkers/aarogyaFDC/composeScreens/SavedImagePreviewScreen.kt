package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.media.Image
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.aarogyaforworkers.aarogyaFDC.Camera.CameraRepository
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
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            val profileUrlWithTimestamp = "${MainActivity.cameraRepo.savedImageView.value!!.imageLink}?t=$timestamp"
            val painter = rememberImagePainter(data = profileUrlWithTimestamp)
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(painter) {
                if (painter.state is ImagePainter.State.Loading) {
                    coroutineScope.launch {
                        while (painter.state is ImagePainter.State.Loading) {
                            delay(10)
                        }
                    }
                }
            }
            Image(
                painter = painter,
                contentDescription = "Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)
//            .rotate(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 90f else 0f)


//            Image(bitmap = MainActivity.cameraRepo.savedImageView.value!!.image,
//                contentDescription = "viewImage",
//                modifier = Modifier.fillMaxSize(),
//                contentScale = ContentScale.Crop)



            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = .5f))
            ) {
                IconButton(onClick = {
                    when(MainActivity.cameraRepo.isAttachmentScreen.value){
                        "PE" -> navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                        "LR" -> navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                        "IP" -> navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                    }
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "backIcon", tint = Color.White)
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .align(Alignment.BottomStart)
                    .background(Color.White.copy(alpha = .5f)) ) {
                RegularTextView(title = MainActivity.cameraRepo.savedImageView.value!!.caption, fontSize = 18, modifier = Modifier.padding(16.dp), textColor = Color.White)
            }
        }
    }
}
