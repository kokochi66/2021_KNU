package com.capston.ocrwordbook.ui.word.dialog



import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import com.capston.ocrwordbook.databinding.DialogAddingFolderBinding




class AddFolderDialog(context : Context, private val onClickConfirmationButton: (String) -> Unit) : Dialog(context)  {
    private lateinit var binding: DialogAddingFolderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogAddingFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)

        getWindow()?.let {
            it.setGravity(Gravity.CENTER)
        }

        initConfirmationButton()


    }
    private fun initConfirmationButton() {
        binding.dialogAddingFolderButtonConfirm.setOnClickListener {
            val folderName = binding.dialogAddingFolderEditTextFolderName.text.toString()
            onClickConfirmationButton(folderName)
            dismiss()
        }
    }
    override fun show() {
        if(!this.isShowing) super.show()
    }
}