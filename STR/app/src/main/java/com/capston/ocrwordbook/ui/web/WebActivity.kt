package com.capston.ocrwordbook.ui.web

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebViewRenderProcess
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.databinding.ActivityWebBinding


class WebActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    var viewModel: WebViewModel = WebViewModel()
    var word: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_NoTitleBar)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_web)
        binding.viewModel = viewModel


        //웹뷰 가로가 fit해서 overview를 볼 수 있게 설정
        binding.webWebView.settings.loadWithOverviewMode = true
        binding.webWebView.settings.useWideViewPort = true

        word = intent.getStringExtra("word") ?: "error"

        //binding.webWebView.webViewClient = WebViewClient()
        binding.webWebView.loadUrl("https://www.wordreference.com/enko/$word")




    }

}