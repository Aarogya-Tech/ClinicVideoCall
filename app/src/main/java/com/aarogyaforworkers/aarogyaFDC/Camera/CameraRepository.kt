package com.aarogyaforworkers.aarogyaFDC.Camera

import android.graphics.Bitmap
import android.util.Log
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

    val pmshImageList : AttachmentRowItem? = null

    private var isPMSHImageList = mutableStateOf(mutableListOf(pmshImageList))

    val  PMSHImageList : State<MutableList<AttachmentRowItem?>>  = isPMSHImageList

    fun updatePMSHImageList(item : AttachmentRowItem){
        isPMSHImageList.value.add(item)
    }


    private var _savedImageView = mutableStateOf<AttachmentPreviewItem?>(null)

    val savedImageView: State<AttachmentPreviewItem?> = _savedImageView

    private var isSelectedPreviewImage : MutableState<Bitmap?> = mutableStateOf(null)

    var selectedPreviewImage : State<Bitmap?> = isSelectedPreviewImage

    fun updateSelectedImage(image : Bitmap?){
        isSelectedPreviewImage.value= image

        Log.i("CameraRepo ","SavedImagePreview "+isSelectedPreviewImage.value.toString())
    }

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



    private val downloadedImage : Bitmap? = null

    private var isDownloadedImagesBitmap = mutableStateOf(mutableListOf(downloadedImage))

    var downloadedImagesBitmap : MutableState<MutableList<Bitmap?>> = isDownloadedImagesBitmap

    var downloadedImagesMap = mutableStateOf(mutableMapOf<String, Bitmap?>())
    fun updateDownloadedImage(key: String, bitmap: Bitmap?){
        downloadedImagesMap.value[key] = bitmap
    }

//    fun updateDownloadedImage(bitmap: Bitmap?){
//        downloadedImagesBitmap.value.add(bitmap)
//    }

    fun clearDownloadedImageBitMap(){
        downloadedImagesBitmap.value.clear()
    }

    var capturedImageBitmap : State<Bitmap?> = isImageCaptured

    var capturedImageFailedState : State<Boolean?> = isImageCapturedFailed

    fun updateCapturedImage(bitmap: Bitmap?){
        isImageCaptured.value = bitmap
        Log.i("CameraRepo ","SavedImagePreview "+isImageCaptured.value.toString())
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