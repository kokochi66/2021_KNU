package com.capston.ocrwordbook.ui.result.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import com.capston.ocrwordbook.databinding.DialogDescriptionBinding

class DescriptionDialog(context : Context) : Dialog(context)  {
    private lateinit var binding: DialogDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window: Window? = getWindow()
        if (window != null) {
            window.setGravity(Gravity.CENTER)
        }

        binding.dialogDescriptionYes.setOnClickListener {
            dismiss()
        }


    }
    override fun show() {
        if(!this.isShowing) super.show()
    }
}