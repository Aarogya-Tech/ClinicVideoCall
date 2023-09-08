package com.aarogyaforworkers.aarogyaFDC.composeScreens.Models

import androidx.compose.ui.graphics.ImageBitmap

data class AttachmentRowItem(val caption:String, val image : ImageBitmap, var isUploaded : Boolean)

data class AttachmentPreviewItem(val caption:String, val imageLink : String)

