package com.capston.ocrwordbook.ui.word.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import com.capston.ocrwordbook.databinding.DialogConfirmationBinding
import com.capston.ocrwordbook.ui.result.ResultActivityView
import com.capston.ocrwordbook.ui.word.WordFragmentView


class ConfirmationDeleteDialog(context : Context, val recognizedWord : String, val position: Int, val wordFragmentView: WordFragmentView) : Dialog(context)  {
    private lateinit var binding: DialogConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)

        val window: Window? = getWindow()
        if (window != null) {
            window.setGravity(Gravity.CENTER)
        }

        binding.dialogConfirmationTextConfirmation.text = "선택한 단어("+recognizedWord+")를\n 정말 삭세하시겠습니까?"

        binding.dialogConfirmationTextYes.setOnClickListener {
            wordFragmentView.onClickDialogYes(position)
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