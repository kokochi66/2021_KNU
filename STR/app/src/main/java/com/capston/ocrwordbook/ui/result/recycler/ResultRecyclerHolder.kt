package com.capston.ocrwordbook.ui.result.recycler

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R


class ResultRecyclerHolder(context: Context, itemView: View)
    : RecyclerView.ViewHolder(itemView) {



    val img = itemView.findViewById<ImageView>(R.id.recycler_item_result_image)
    val recognizedText = itemView.findViewById<TextView>(R.id.recycler_item_result_recognized_text)
    val meaningText = itemView.findViewById<TextView>(R.id.recycler_item_result_meaning_text)


    val context = context

    fun bindView(item : ResultRecyclerItem) {
        img.setImageBitmap(item.img)
        recognizedText.text = item.recognizedText
        meaningText.text = item.meaningText
    }


}