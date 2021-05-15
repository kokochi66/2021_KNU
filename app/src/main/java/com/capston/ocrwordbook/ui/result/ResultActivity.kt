package com.capston.ocrwordbook.ui.result

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.databinding.ActivityMainBinding
import com.capston.ocrwordbook.databinding.ActivityResultBinding
import com.capston.ocrwordbook.ui.camera.CameraFragment
import com.capston.ocrwordbook.ui.main.MainViewModel
import com.capston.ocrwordbook.ui.word.WordFragment


class ResultActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    var viewModel: ResultViewModel = ResultViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_NoTitleBar)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this , R.layout.activity_result)
        binding.viewModel = viewModel

        binding.resultImage.setImageURI(MainViewModel.onGetPicture.value)



    }

}