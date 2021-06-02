package com.capston.ocrwordbook.utils


import android.R
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.capston.ocrwordbook.databinding.DialogLoadingBinding
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.ThreeBounce


class LoadingDialog(context: Context) : Dialog(context) {
    private lateinit var binding: DialogLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable())
        window!!.setDimAmount(0.2f)

        var progressBar = binding.spinKit
        var threeBounce: Sprite = ThreeBounce()
        progressBar.setIndeterminateDrawable(threeBounce)
    }

    override fun show() {
        if(!this.isShowing) super.show()
    }
}