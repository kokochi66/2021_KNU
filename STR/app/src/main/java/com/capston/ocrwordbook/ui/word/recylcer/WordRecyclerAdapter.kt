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
) : ListAdapter<Word, WordRecyclerAdapter.ViewHolder>(diffUtil), Filterable {

    val wordComparator = Comparator<Word> { o1, o2 ->
        if(o1.isWord != o2.isWord) {
            o2.isWord.compareTo(o1.isWord)
        } else if(o1.favorite != o2.favorite) {
            o2.favorite.compareTo(o1.favorite)
        } else {
            o1.word.compareTo(o2.word)
        }
    }

    inner class ViewHolder(private val binding: RecyclerItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {

            binding.recyclerItemWordTextWord.text = word.word
            binding.recyclerItemWordTextMeaning.text = word.meaning
            if (!word.isWord) {
                binding.recyclerItemWordButtonFavorite.setImageResource(R.drawable.word_list_move_to_folder)
            } else if (word.favorite) {
                binding.recyclerItemWordButtonFavorite.setImageResource(R.drawable.favorite_filled)
            } else {
                binding.recyclerItemWordButtonFavorite.setImageResource(R.drawable.favorite_empty)
            }

            binding.recyclerItemWordButtonFavorite.setOnClickListener {
                word.favorite = !word.favorite
                notifyDataSetChanged()
            }

            binding.root.setOnClickListener {
                onClickWord(word)
            }
            binding.root.setOnLongClickListener {
                onLongClickWord(word)
                true
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val keyword = constraint.toString()
                val filterResults = FilterResults()
                if (keyword.isNotEmpty()) {
                    val filteredList = mutableListOf<Word>()

                    currentList.forEach {
                        if (it.word.equals(keyword, true)) {
                            filteredList.add(it)
                        }
                    }
                    filterResults.values = filteredList
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                submitList(results.values as List<Word>)
            }
        }
    }

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






















