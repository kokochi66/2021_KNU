package com.capston.ocrwordbook.ui.word.recylcer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.data.WordbookItem
import com.capston.ocrwordbook.databinding.RecyclerItemWordBinding


class WordbookItemRecyclerAdapter(
    private val onClickWordbookItem: (WordbookItem) -> Unit,
    private val onLongClickWordbookItem: (WordbookItem) -> Unit
) : ListAdapter<WordbookItem, WordbookItemRecyclerAdapter.ViewHolder>(diffUtil), Filterable {


    inner class ViewHolder(private val binding: RecyclerItemWordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wordbookItem: WordbookItem) {

            binding.recyclerItemWordTextWord.text = wordbookItem.word
            binding.recyclerItemWordTextMeaning.text = wordbookItem.meaning
            if (!wordbookItem.isWord) {
                binding.recyclerItemWordButtonFavorite.setImageResource(R.drawable.word_list_move_to_folder)
            } else if (wordbookItem.favorite) {
                binding.recyclerItemWordButtonFavorite.setImageResource(R.drawable.favorite_filled)
            } else {
                binding.recyclerItemWordButtonFavorite.setImageResource(R.drawable.favorite_empty)
            }

            binding.recyclerItemWordButtonFavorite.setOnClickListener {
                wordbookItem.favorite = !wordbookItem.favorite
                notifyDataSetChanged()
            }

            binding.root.setOnClickListener {
                onClickWordbookItem(wordbookItem)
            }
            binding.root.setOnLongClickListener {
                onLongClickWordbookItem(wordbookItem)
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
                    val filteredList = mutableListOf<WordbookItem>()

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
                submitList(results.values as List<WordbookItem>)
            }
        }
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<WordbookItem>() {
            // id 값만 비교
            override fun areItemsTheSame(oldItem: WordbookItem, newItem: WordbookItem): Boolean =
                oldItem.uid == newItem.uid


            // 실제 데이터클래스 안의 컨텐츠 (프로퍼티의 값) 을 비교
            override fun areContentsTheSame(oldItem: WordbookItem, newItem: WordbookItem): Boolean =
                oldItem == newItem
        }
    }
}






















