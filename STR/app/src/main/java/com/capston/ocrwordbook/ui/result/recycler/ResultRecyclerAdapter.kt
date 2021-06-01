package com.capston.ocrwordbook.ui.result.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.ui.web.WebViewModel
import com.capston.ocrwordbook.ui.word.recylcer.WordRecyclerHolder
import com.capston.ocrwordbook.ui.word.recylcer.WordRecyclerItem



class ResultRecyclerAdapter(private val context: Context?, val itemList: ArrayList<ResultRecyclerItem>)
    : RecyclerView.Adapter<ResultRecyclerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultRecyclerHolder {
        return ResultRecyclerHolder(context!!,
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_result, parent, false))

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ResultRecyclerHolder, position: Int) {
        holder.bindView(itemList[position])

    }
}