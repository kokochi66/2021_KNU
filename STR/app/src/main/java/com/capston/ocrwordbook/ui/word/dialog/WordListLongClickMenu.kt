package com.capston.ocrwordbook.ui.word.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import com.capston.ocrwordbook.databinding.DialogConfirmationBinding
import com.capston.ocrwordbook.databinding.DialogWordListLongClickMenuBinding
import com.capston.ocrwordbook.ui.word.WordFragmentView


class WordListLongClickMenu(
    context : Context,
    private val onClickDeleteButton: ()-> Unit,
    private val onClickMoveFolderButton: () -> Unit) : Dialog(context)  {
    private lateinit var binding: DialogWordListLongClickMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogWordListLongClickMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)

        val window: Window? = getWindow()
        if (window != null) {
            window.setGravity(Gravity.CENTER)
        }

        binding.dialogWordListLongClickMenuDelete.setOnClickListener {
            onClickDeleteButton()
            dismiss()
        }

        binding.dialogWordListLongClickMenuFolder.setOnClickListener {
            onClickMoveFolderButton()
            dismiss()
        }

    }
    override fun show() {
        if(!this.isShowing) super.show()
    }

}