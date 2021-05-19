package com.capston.ocrwordbook.ui.word.recylcer

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R


class WordRecyclerHolder(context: Context, itemView: View)
    : RecyclerView.ViewHolder(itemView) {




    val word = itemView.findViewById<TextView>(R.id.recycler_item_word_text_word)
    val meaning = itemView.findViewById<TextView>(R.id.recycler_item_word_text_meaning)
    val favorite = itemView.findViewById<ImageView>(R.id.recycler_item_word_button_favorite)

    val context = context

    fun bindView(item : WordRecyclerItem) {
        word.text = item.word
        meaning.text = item.meaning
        if(item.favorite) {
            favorite.setImageResource(R.drawable.favorite_filled)
        } else {
            favorite.setImageResource(R.drawable.favorite_empty)
        }





    }


}