package com.capston.ocrwordbook.ui.result.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import com.capston.ocrwordbook.databinding.DialogConfirmationBinding
import com.capston.ocrwordbook.ui.result.ResultViewModel

class ConfirmationDialog(context : Context, val recognizedWord : String) : Dialog(context)  {
    private lateinit var binding: DialogConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)

        val window: Window? = getWindow()
        if (window != null) {
            window.setGravity(Gravity.CENTER)
        }

        binding.dialogConfirmationTextConfirmation.text = "선택한 단어("+recognizedWord+")을\n저장하시겠습니까?"

        binding.dialogConfirmationTextYes.setOnClickListener {
            ResultViewModel.onClickConfirmation.value = false
            dismiss()
        }

        binding.dialogConfirmationTextNo.setOnClickListener {
            dismiss()
        }


    }
    override fun show() {
        if(!this.isShowing) super.show()
    }
}