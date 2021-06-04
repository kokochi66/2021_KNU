package com.capston.ocrwordbook.ui.result.recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.ui.result.ResultActivityView
import com.capston.ocrwordbook.ui.result.ResultViewModel
import com.capston.ocrwordbook.ui.result.dialog.ConfirmationSaveDialog
import com.capston.ocrwordbook.ui.web.WebActivity
import com.capston.ocrwordbook.ui.web.WebViewModel


class ResultRecyclerAdapter(private val context: Context?, val itemList: ArrayList<ResultRecyclerItem>, val resultActivityView: ResultActivityView)
    : RecyclerView.Adapter<ResultRecyclerHolder>() {

    lateinit var mConfSaveDialog: ConfirmationSaveDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultRecyclerHolder {
        return ResultRecyclerHolder(context!!,
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_result, parent, false))

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ResultRecyclerHolder, position: Int) {
        holder.bindView(itemList[position])

        val container = holder.itemView.findViewById<ConstraintLayout>(R.id.recycler_item_result_container)

        container.setOnClickListener {

            WebViewModel.onClickWord.value = itemList[position].recognizedText
            val intent = Intent(context, WebActivity::class.java)
            context?.startActivity(intent)

        }

        container.setOnLongClickListener {
            ResultViewModel.recognizedText.value = itemList[position].recognizedText
            ResultViewModel.meaningText.value = itemList[position].meaningText
            showConfSaveDialog(context!!, itemList[position].recognizedText)

            true
        }
    }

    fun showConfSaveDialog(context: Context, recognizedWord: String) {
        mConfSaveDialog = ConfirmationSaveDialog(context,recognizedWord, resultActivityView)
        mConfSaveDialog.show()
    }



}