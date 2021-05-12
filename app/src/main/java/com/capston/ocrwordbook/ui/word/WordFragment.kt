package com.capston.ocrwordbook.ui.word

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.config.BaseFragment
import com.capston.ocrwordbook.databinding.FragmentWordBinding
import com.capston.ocrwordbook.ui.word.recylcer.WordRecyclerItem
import com.capston.ocrwordbook.ui.word.recylcer.WordRecyclerAdapter

class WordFragment : BaseFragment<FragmentWordBinding, WordViewModel>(R.layout.fragment_word) {

    private var recyclerWordList = ArrayList<WordRecyclerItem>()
    private lateinit var recyclerWordAdapter  : WordRecyclerAdapter

    override var viewModel: WordViewModel = WordViewModel()
    override fun setViewModel() {
        binding.viewModel = viewModel
    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)


        recyclerWordList.add(WordRecyclerItem("영어", "한국어", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("apple", "사과", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("banana", "바나나", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("melon", "멜론", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("영어", "한국어", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("apple", "사과", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("banana", "바나나", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("melon", "멜론", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("영어", "한국어", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("apple", "사과", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("banana", "바나나", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("melon", "멜론", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("영어", "한국어", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("apple", "사과", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("banana", "바나나", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("melon", "멜론", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("영어", "한국어", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("apple", "사과", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("banana", "바나나", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("melon", "멜론", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("영어", "한국어", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("apple", "사과", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("banana", "바나나", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("melon", "멜론", false, "미정"))
        recyclerWordList.add(WordRecyclerItem("영어", "한국어", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("apple", "사과", true, "미정"))
        recyclerWordList.add(WordRecyclerItem("banana", "바나나", false, "미정"))





        binding.wordRecyclerView.apply {
            adapter = WordRecyclerAdapter(context, recyclerWordList)
            layoutManager = GridLayoutManager(context, 1)
        }


        recyclerWordList.add(WordRecyclerItem("last", "마지막", false, "미정"))


        return binding.root
    }

}