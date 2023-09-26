package com.aarogyaforworkers.aarogyaFDC.composeScreens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.*
import com.aarogyaforworkers.aarogyaFDC.Camera.CameraRepository
import com.aarogyaforworkers.aarogyaFDC.MainActivity
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.ImageWithCaptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.checkerframework.checker.units.qual.UnitsRelations
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun LoadImageFromUrl(image: String,onImageLoaded:()->Unit) {

    val url = image

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val scope = rememberCoroutineScope()

    val downloadedImageList = MainActivity.cameraRepo.downloadedImagesBitmap.value

    DisposableEffect(url) {
        scope.launch {
            try {
                val fetchedBitmap = withContext(Dispatchers.IO) {
                    fetchImageFromUrl(url)
                }
                MainActivity.cameraRepo.updateCapturedImage(fetchedBitmap)
                MainActivity.cameraRepo.updateSelectedImage(fetchedBitmap)
                MainActivity.cameraRepo.updateDownloadedImage(url, fetchedBitmap)
                onImageLoaded()
//                bitmap = fetchedBitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        onDispose {}
    }

//    bitmap?.let { loadedBitmap ->
//        Log.d("TAG", "LoadImageFromUrl: fetched image $loadedBitmap ")
////        MainActivity.cameraRepo.updateDownloadedImage(loadedBitmap)
//    }
}

@Composable
fun LoadImagesSequentially(
    images: List<ImageWithCaptions>,
    onImageDownloaded: (Bitmap) -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    DisposableEffect(currentIndex) {
        if (currentIndex < images.size) {
            val imageUrl = images[currentIndex].imageLink
            if(!MainActivity.cameraRepo.downloadedImagesMap.value.keys.contains(imageUrl)) {
                scope.launch {
                    try {
                        val bitmap = withContext(Dispatchers.IO) {
                            fetchImageFromUrl(imageUrl)
                        }
                        if(!MainActivity.cameraRepo.downloadedImagesMap.value.keys.contains(imageUrl)){
                            MainActivity.cameraRepo.updateDownloadedImage(imageUrl, bitmap)
                            onImageDownloaded(bitmap)
                        }else{
                            currentIndex++
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    currentIndex++
                }
            }else{
                currentIndex++
            }
        }
        onDispose {}
    }
}

// Rest of the code remains the same...


 public fun fetchImageFromUrl(urlString: String): Bitmap {
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    connection.connectTimeout = 5000
    connection.readTimeout = 5000
    connection.requestMethod = "GET"
    connection.connect()
    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
        val inputStream: InputStream = connection.inputStream
        return BitmapFactory.decodeStream(inputStream)
    } else {
        throw IOException("LoadImageFromUrl: Failed to fetch image from URL: $urlString")
    }
}