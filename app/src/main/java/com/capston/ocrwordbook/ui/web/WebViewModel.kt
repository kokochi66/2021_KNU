package com.capston.ocrwordbook.ui.web

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WebViewModel : ViewModel() {
    companion object {
        var onClickWord : MutableLiveData<String> = MutableLiveData<String>()
        var onClickWordItem : MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    }
}