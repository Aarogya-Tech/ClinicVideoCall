import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun downloadFile(
    url: String,
    destinationFile: File,
    onComplete: (File) -> Unit,
    onError: (String) -> Unit
) {
    // Check if the file already exists at the destination
    if (destinationFile.exists()) {
        onComplete(destinationFile)
        return
    }

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                val outputStream = FileOutputStream(destinationFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                        onComplete(destinationFile)
                    }
                }
            } else {
                onError("File download failed")
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            onError("File download failed: ${e.message}")
        }
    })
}


fun openDocuments(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(uri, "application/pdf")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

