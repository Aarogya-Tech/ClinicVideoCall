package com.aarogyaforworkers.aarogyaFDC.composeScreens.Models

import android.net.Uri

data class ImageWithCaptions(var caption : String, var imageLink : String)

data class Pdf(var name : String, var pdfLink : String)

data class DocumentInfo(
    val name: String,
    val uri: Uri
)

