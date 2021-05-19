package com.capston.ocrwordbook.ui.result

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {

    companion object {

    }

    fun setImage(img : ImageView,bitMap : Bitmap) {
        img.setImageBitmap(bitMap)
    }

}