package com.capston.ocrwordbook.ui.word.recylcer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.data.Word
import com.capston.ocrwordbook.databinding.RecyclerItemWordBinding


class WordRecyclerAdapter(
    private val onClickWord: (Word) -> Unit,
    private val onLongClickWord: (Word) -> Unit
) : ListAdapter<Word, WordRecyclerAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: RecyclerItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {

            binding.recyclerItemWordTextWord.text = word.word
            binding.recyclerItemWordTextMeaning.text = word.meaning
            if (word.favorite) {
                binding.recyclerItemWordButtonFavorite.setImageResource(R.drawable.favorite_filled)
            } else {
                binding.recyclerItemWordButtonFavorite.setImageResource(R.drawable.favorite_empty)
            }

            binding.recyclerItemWordButtonFavorite.setOnClickListener {
                word.favorite = !word.favorite
                notifyDataSetChanged()
            }

            binding.root.setOnClickListener {
                onClickWord(word) // todo 웹뷰를 보여준다.
            }
            binding.root.setOnClickListener {
                onLongClickWord(word)  // todo 단어를 저장한다.
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            RecyclerItemWordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence): FilterResults {
//                val keyword = constraint.toString()
//                val filterResults = FilterResults()
//                if (keyword.isNotEmpty()) {
//                    val filteredList = mutableListOf<Word>()
//
//                    currentList.forEach {
//                        if (it.word.equals(keyword, true)) {
//                            filteredList.add(it)
//                        }
//                    }
//                    filterResults.values = filteredList
//                }
//                return filterResults
//            }
//
//            override fun publishResults(constraint: CharSequence, results: FilterResults) {
//                submitList(results.values as List<Word>)
//            }
//        }
//    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<Word>() {
            // id 값만 비교
            override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean =
                oldItem.id == newItem.id


            // 실제 데이터클래스 안의 컨텐츠 (프로퍼티의 값) 을 비교
            override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean =
                oldItem == newItem
        }
    }
}






















