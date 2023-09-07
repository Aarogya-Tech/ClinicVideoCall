package com.aarogyaforworkers.aarogyaFDC.Camera

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentPreviewItem
import com.aarogyaforworkers.aarogyaFDC.composeScreens.Models.AttachmentRowItem
import com.aarogyaforworkers.awsapi.models.Session

class CameraRepository {

    private var isImageCaptured : MutableState<Bitmap?> = mutableStateOf(null)

    private var isImageCapturedFailed : MutableState<Boolean?> = mutableStateOf(false)

    val peImageList : AttachmentRowItem? = null

    private var isPEImageList = mutableStateOf(mutableListOf(peImageList))

    val  PEImageList : State<MutableList<AttachmentRowItem?>>  = isPEImageList

    fun updatePEImageList(item : AttachmentRowItem){
        isPEImageList.value.add(item)
    }

    val lrImageList : AttachmentRowItem? = null

    private var isLRImageList = mutableStateOf(mutableListOf(lrImageList))

    val  LRImageList : State<MutableList<AttachmentRowItem?>>  = isLRImageList

    fun updateLRImageList(item : AttachmentRowItem){
        isLRImageList.value.add(item)
    }

    val ipImageList : AttachmentRowItem? = null

    private var isIPImageList = mutableStateOf(mutableListOf(ipImageList))

    val  IPImageList : State<MutableList<AttachmentRowItem?>>  = isIPImageList

    fun updateIPImageList(item : AttachmentRowItem){
        isIPImageList.value.add(item)
    }

    private var _savedImageView = mutableStateOf<AttachmentPreviewItem?>(null)

    val savedImageView: State<AttachmentPreviewItem?> = _savedImageView

    fun updateSavedImageView(item: AttachmentPreviewItem) {
        _savedImageView.value = item
    }

    private var _attachmentScreen:MutableState<String> = mutableStateOf("")
    val isAttachmentScreen:State<String> = _attachmentScreen

    fun updateAttachmentScreenNo(screenNo: String){
        _attachmentScreen.value = screenNo
    }


//    val savedImageView : AttachmentRowItem? = null
//
//    private var isSavedImageView = mutableStateOf(mutableListOf(savedImageView))
//
//    var  SavedImageView : State<MutableList<AttachmentRowItem?>>  = isSavedImageView
//
//    fun updateSavedImageView(item : AttachmentRowItem){
//        isSavedImageView.value.add(item)
//    }


    var capturedImageBitmap : State<Bitmap?> = isImageCaptured

    var capturedImageFailedState : State<Boolean?> = isImageCapturedFailed

    fun updateCapturedImage(bitmap: Bitmap){
        isImageCaptured.value = bitmap
    }

    fun onImageClickFailed(isFailed : Boolean){
        isImageCapturedFailed.value = isFailed
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: CameraRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CameraRepository().also { instance = it }
            }
    }
}