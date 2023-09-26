package com.aarogyaforworkers.aarogya.composeScreens

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogyaFDC.Camera.CameraRepository
import com.aarogyaforworkers.aarogyaFDC.Destination
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Disableback
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.zIndex


@Composable
fun CameraScreen(cameraRepository: CameraRepository, navHostController: NavHostController) {
    Disableback()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    SimpleCameraPreview(
        context = context,
        lifecycleOwner = lifecycleOwner,
        cameraRepository, navHostController
    )
}


@Composable
fun SimpleCameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner, cameraRepository: CameraRepository, navHostController: NavHostController
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    Log.d("CameraConfig", "Initializing camera provider")

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var preview by remember { mutableStateOf<androidx.camera.core.Preview?>(null) }
    val camera: Camera? = null
    val executor = ContextCompat.getMainExecutor(context)
    val cameraProvider = cameraProviderFuture.get()


    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var bitmap =  remember {
        mutableStateOf<Bitmap?>(null)
    }

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Box {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .apply {
                            setAnalyzer(executor, FaceAnalyzer())
                        }
                    Log.d("CameraConfig", "Image analysis initialized")

                    imageCapture = ImageCapture.Builder()
                        .setTargetRotation(previewView!!.display.rotation)
                        .build()
                    Log.d("CameraConfig", "Image capture initialized")


                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageCapture,
                        preview
                    )
                    Log.d("CameraConfig", "Camera bound to lifecycle with selector: $cameraSelector")

                }, executor)
                preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView!!.surfaceProvider)
                }
                previewView!!
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .align(Alignment.TopStart)
        ) {
            IconButton(
                onClick = {
                    when(MainActivity.cameraRepo.isAttachmentScreen.value){
                        "PE" -> navHostController.navigate(Destination.PhysicalExaminationScreen.routes)
                        "LR" -> navHostController.navigate(Destination.LaboratoryRadiologyScreen.routes)
                        "IP" -> navHostController.navigate(Destination.ImpressionPlanScreen.routes)
                        "PMSH" -> navHostController.navigate(Destination.PastMedicalSurgicalHistoryScreen.routes)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "back arrow",
                    tint = Color.White
                )
            }
        }
        Box(modifier=Modifier.align(Alignment.BottomCenter)) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier=Modifier.zIndex(1f).align(Alignment.CenterStart).padding(horizontal = 24.dp)
            ) {

                IconButton(onClick = {
                    launcher.launch("image/*")
                }
                ) {
                    Icon(imageVector = Icons.Default.Image, contentDescription = "Gallery Icon",modifier=Modifier.size(60.dp),tint= Color.White)
                }

                imageUri?.let {
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap.value = MediaStore.Images
                            .Media.getBitmap(context.contentResolver,it)

                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver,it)
                        bitmap.value = ImageDecoder.decodeBitmap(source)
                    }

//                    bitmap.value?.let {  btm ->
//                        Image(bitmap = btm.asImageBitmap(),
//                            contentDescription =null,
//                            modifier = Modifier.size(400.dp))
//                    }

                    if (bitmap.value != null) {
//                                    bitmap = rotateBitmap(bitmap, 90f)
                        val byteCount = bitmap.value!!.allocationByteCount
                        val sizeInMB = byteCount.toFloat() / (1024f * 1024f)
                        Log.d("TAG", "Image Size: $sizeInMB MB")
                        val compressedBitmap = compressBitmap(bitmap.value!!, 80)
                        cameraRepository.updateCapturedImage(compressedBitmap)
                        imageUri=null
                        navHostController.navigate(Destination.ImagePreviewScreen.routes)
                    }else{
//                        cameraRepository.onImageClickFailed(true)
                    }

                }

            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.DarkGray, RoundedCornerShape(15.dp))
                    .padding(8.dp)
            ) {
                Button(
                    onClick = {
                        val imgCapture = imageCapture ?: return@Button

                        imgCapture.takePicture(executor, @ExperimentalGetImage object : ImageCapture.OnImageCapturedCallback(){
                            override fun onCaptureSuccess(image: ImageProxy) {
                                super.onCaptureSuccess(image)
                                val image = image.image ?: return

                                val buffer = image.planes[0].buffer
                                val bytes = ByteArray(buffer.remaining())
                                buffer.get(bytes)
                                val options = BitmapFactory.Options().apply {
                                    inSampleSize = 4 // reduces the size to 1/4th of original
                                }
                                var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                                if (bitmap != null) {
                                    var angle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 90f else 0f
                                    bitmap = rotateBitmap(bitmap, angle)
                                    val byteCount = bitmap.allocationByteCount
                                    val sizeInMB = byteCount.toFloat() / (1024f * 1024f)
                                    Log.d("TAG", "Image Size: $sizeInMB MB")
                                    val compressedBitmap = compressBitmap(bitmap, 80)
                                    cameraRepository.updateCapturedImage(compressedBitmap)
                                    navHostController.navigate(Destination.ImagePreviewScreen.routes)
                                }else{
                                    cameraRepository.onImageClickFailed(true)
                                }
                                image.close()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                super.onError(exception)
                                Log.e("CameraConfig", "Error capturing image: ${exception.message}", exception)
                                cameraRepository.onImageClickFailed(true)
                            }
                        })
                    },
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color.LightGray, CircleShape)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .border(5.dp, Color.LightGray, CircleShape),
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                ) {
                }
            }
        }

    }
}



// Function to resize a Bitmap
fun resizeBitmap(originalBitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
}

fun calculateResizedDimensions(originalWidth: Int, originalHeight: Int, targetSizeInMB: Float): Pair<Int, Int> {
    val originalSizeInBytes = originalWidth * originalHeight * 4 // Assuming ARGB_8888 format (4 bytes per pixel)
    val targetSizeInBytes = targetSizeInMB * 1024 * 1024 // Convert MB to bytes
    val resizeFactor = sqrt(originalSizeInBytes.toFloat() / targetSizeInBytes.toFloat())
    val newWidth = (originalWidth / resizeFactor).toInt()
    val newHeight = (originalHeight / resizeFactor).toInt()
    return Pair(newWidth, newHeight)
}


fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    val compressedBytes = outputStream.toByteArray()
    val sizeInBytes = compressedBytes.size
    val sizeInMB = sizeInBytes / (1024f * 1024f)
    // Print the size for each iteration
    Log.d("TAG", "Image Size: compressed (Quality: $quality): $sizeInMB MB")
    return BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
}

private fun compressBitmapToTargetSize(bitmap: Bitmap, targetSizeInMB: Float): Bitmap {
    val outputStream = ByteArrayOutputStream()
    var quality = 80 // Start with maximum quality (0-100)
    var compressedBitmap = bitmap

    do {
        outputStream.reset()
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedBytes = outputStream.toByteArray()
        compressedBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)

        // Calculate the resulting file size in MB
        val sizeInBytes = compressedBytes.size
        val sizeInMB = sizeInBytes / (1024f * 1024f)

        // Print the size for each iteration
        Log.d("TAG", "compressBitmapToTargetSize: (Quality: $quality): $sizeInMB MB")

        // Adjust quality for the next iteration
        quality -= 10 // Decrease quality in each iteration, adjust as needed

        // Break the loop if the size is within the target or quality becomes too low
    } while (sizeInMB > targetSizeInMB && quality >= 0)

    return compressedBitmap
}




fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
    val matrix = android.graphics.Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}

class FaceAnalyzer(): ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        Log.d("CameraConfig", "Face analysis started on image with rotation: ${image.imageInfo.rotationDegrees}")
        val imagePic = image.image
        imagePic?.close()
    }
}



