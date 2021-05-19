package com.capston.ocrwordbook.ui.word.recylcer

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.ui.web.WebViewModel


class WordRecyclerAdapter(private val context: Context?, val itemList: ArrayList<WordRecyclerItem>)
    : RecyclerView.Adapter<WordRecyclerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordRecyclerHolder {
        return WordRecyclerHolder(context!!,
                LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_word, parent, false))

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: WordRecyclerHolder, position: Int) {
        holder.bindView(itemList[position])
        var container = holder.itemView.findViewById<ConstraintLayout>(R.id.recycler_item_word_container)
        container.setOnClickListener {
            WebViewModel.onClickWord.value = itemList[position].word
            WebViewModel.onClickWordItem.value = true
        }

    }
}