package com.capston.ocrwordbook.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.ui.camera.CameraFragment
import java.net.URI

class MainViewModel() : ViewModel() {

    companion object {
        var onCropPicture : MutableLiveData<Uri> = MutableLiveData<Uri>()
        var onGetPicture : MutableLiveData<Uri> = MutableLiveData<Uri>()
        var onClickGalleryButton : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val PICK_IMAGE = 0
    }

}