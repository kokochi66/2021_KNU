package com.capston.ocrwordbook.ui.word.recylcer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.ui.result.dialog.ConfirmationSaveDialog
import com.capston.ocrwordbook.ui.web.WebViewModel
import com.capston.ocrwordbook.ui.word.WordFragmentView
import com.capston.ocrwordbook.ui.word.dialog.ConfirmationDeleteDialog
import com.capston.ocrwordbook.utils.Resource


class WordRecyclerAdapter(private val context: Context?, val wordList: ArrayList<WordRecyclerItem>, val wordFragmentView: WordFragmentView)
    : RecyclerView.Adapter<WordRecyclerHolder>(), Filterable {

    //필터링을 위해 필요한 변수
    var itemList: ArrayList<WordRecyclerItem> = wordList

//    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val str: TextView = itemView.
//
//    }

    lateinit var mConfDeleteDialog: ConfirmationDeleteDialog

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

        // 웹뷰 화면으로 이동
        container.setOnClickListener {
            WebViewModel.onClickWord.value = itemList[position].word
            WebViewModel.onClickWordItem.value = true
        }

        // 길게누르면 다이얼로그 보여주고 삭제
        container.setOnLongClickListener {
           showConfDeleteDialog(context!!, itemList[position].word, position, wordFragmentView)
            true
        }

        var favorite = holder.itemView.findViewById<ImageView>(R.id.recycler_item_word_button_favorite)
        favorite.setOnClickListener {
            if(!itemList[position].favorite) {
                favorite.setImageResource(R.drawable.favorite_empty)
                itemList[position].favorite = true
            }
            else {
                favorite.setImageResource(R.drawable.favorite_filled)
                itemList[position].favorite = false
            }
            itemList.sortWith(compareBy( {it.favorite}, {it.word} ))
            notifyDataSetChanged()
        }

    }



    fun showConfDeleteDialog(context: Context, recognizedWord: String, position: Int, wordFragmentView: WordFragmentView) {
        mConfDeleteDialog = ConfirmationDeleteDialog(context, recognizedWord, position, wordFragmentView)
        mConfDeleteDialog.show()
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val charString = constraint.toString()
                itemList = if (charString.isEmpty()) {
                    wordList
                } else {
                    val filteredList = ArrayList<WordRecyclerItem>()
                    if (itemList != null) {
                        for (wordRecyclerItem in itemList) {
                            if(wordRecyclerItem.word.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(wordRecyclerItem)
                            }
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = itemList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                itemList  = results.values as ArrayList<WordRecyclerItem>
                notifyDataSetChanged()
            }
        }
    }


}