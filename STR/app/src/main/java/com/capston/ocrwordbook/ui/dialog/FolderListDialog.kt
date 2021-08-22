package com.capston.ocrwordbook.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.capston.ocrwordbook.data.Folder
import com.capston.ocrwordbook.databinding.DialogFolderListBinding
import com.capston.ocrwordbook.ui.adapter.FolderRecyclerAdapter


class FolderListDialog(
    context: Context,
    private val folders: List<Folder>,
    private val onClickFolder: (Folder) -> Unit
) : Dialog(context) {
    private lateinit var binding: DialogFolderListBinding
    private lateinit var folderRecyclerAdapter: FolderRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogFolderListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)

        window?.setGravity(Gravity.CENTER)
        initRecyclerView()

    }

    private fun initRecyclerView() {
        folderRecyclerAdapter = FolderRecyclerAdapter {
            onClickFolder(it)
            dismiss()
        }
        binding.dialogFolderListRecyclerView.apply {
            adapter = folderRecyclerAdapter
            layoutManager = LinearLayoutManager(context)
        }

        folderRecyclerAdapter.submitList(folders)

    }

    override fun show() {
        if (!this.isShowing) super.show()
    }

}