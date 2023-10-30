package com.aarogyaforworkers.aarogyaFDC.VideoCall

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ImageDownloadingService : IntentService("ImageDownloadService") {

    override fun onHandleIntent(intent: Intent?) {
        // Retrieve the image URL from the intent
        val imageUrl = intent?.getStringExtra("image_url")
        if (!imageUrl.isNullOrEmpty()) {
            try {
                // Download the image
                val bitmap = downloadImage(imageUrl)
                // Save the downloaded image to external storage or any desired location
                saveImage(bitmap)
                // Broadcast a success message or update UI with the downloaded image
                // For example, you can use LocalBroadcastManager or a callback to communicate with the UI.
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the error, broadcast an error message, or log the error as needed.
            }
        }
    }

    private fun downloadImage(imageUrl: String): Bitmap {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()
        val inputStream: InputStream = connection.inputStream
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun saveImage(bitmap: Bitmap) {
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val fileName = "downloaded_image.jpg"
        val imageFile = File(storageDir, fileName)
        try {
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
