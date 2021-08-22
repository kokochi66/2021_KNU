package com.capston.ocrwordbook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.data.Folder
import com.capston.ocrwordbook.databinding.RecyclerItemFolderBinding

class FolderRecyclerAdapter(
    val onClickFolder: (Folder) -> Unit
) : ListAdapter<Folder, FolderRecyclerAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: RecyclerItemFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: Folder) {
            binding.recyclerItemFolderTextTitle.text = data.folderName
        }
        fun bindView(data: Folder) {
            binding.root.setOnClickListener {
                onClickFolder(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            RecyclerItemFolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(currentList[position])
        holder.bindView(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Folder>() {
            override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean =
                oldItem.uid == newItem.uid

            override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean =
                oldItem == newItem
        }
    }
}