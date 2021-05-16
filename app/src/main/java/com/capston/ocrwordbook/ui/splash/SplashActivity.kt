package com.capston.ocrwordbook.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.databinding.ActivityResultBinding
import com.capston.ocrwordbook.databinding.ActivitySlashBinding
import com.capston.ocrwordbook.ui.main.MainActivity
import com.capston.ocrwordbook.ui.main.MainViewModel
import com.capston.ocrwordbook.ui.result.ResultActivity
import com.capston.ocrwordbook.ui.result.ResultViewModel
import com.capston.ocrwordbook.ui.web.WebViewModel
import java.lang.Thread.sleep
import kotlin.concurrent.thread


class SplashActivity() : AppCompatActivity() {
    private lateinit var binding: ActivitySlashBinding
    var viewModel: SplashViewModel = SplashViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_NoTitleBar)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this , R.layout.activity_slash)
        binding.viewModel = viewModel

        thread {
            sleep(2000)
            runOnUiThread {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }




    }

}