package com.capston.ocrwordbook.ui.main

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.ui.camera.CameraFragment

class MainViewModel() : ViewModel() {

    companion object {
        var onSaved : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    }


    fun CameraToResult(id: Int, context: AppCompatActivity) {
        context.supportFragmentManager.beginTransaction().replace(R.id.main_fragment, CameraFragment()).commit()
    }





}