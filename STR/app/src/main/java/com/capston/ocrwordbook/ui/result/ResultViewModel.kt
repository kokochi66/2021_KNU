package com.capston.ocrwordbook.ui.result

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {

    companion object {
        var onClickWord : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var onClickConfirmation : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var recognizedText : MutableLiveData<String> = MutableLiveData<String>()
        var meaningText : MutableLiveData<String> = MutableLiveData<String>()


    }

    fun setImage(img : ImageView,bitMap : Bitmap) {
        img.setImageBitmap(bitMap)
    }

}