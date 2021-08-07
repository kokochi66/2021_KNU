package com.capston.ocrwordbook.ui.word

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.data.Word
import com.capston.ocrwordbook.databinding.FragmentWordsBinding
import com.capston.ocrwordbook.ui.word.recylcer.WordRecyclerAdapter


/**
 * 저장한 단어를 리스트형태로 볼 수 있다.
 * 짧게 누르면 웹사전 화면으로 이동한다.
 * 길게 누르면 삭제 또는 폴더를 선택할 수 있게 해준다. ( 폴더 안의 폴더는 우선 계획 없음 )
 */
class WordFragment : Fragment(R.layout.fragment_words) {
    private var binding: FragmentWordsBinding? = null
    private lateinit var recyclerWordAdapter: WordRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentWordsBinding = FragmentWordsBinding.bind(view)
        binding = fragmentWordsBinding

        initRecyclerView(fragmentWordsBinding)
        //initSearchEditText(fragmentWordsBinding)


        // todo DB 에 있는 데이터 불러오기

    }

    private fun initRecyclerView(localBinding: FragmentWordsBinding) {
        recyclerWordAdapter = WordRecyclerAdapter(
            onClickWord = {
                          Toast.makeText(context, "item clicked", Toast.LENGTH_LONG).show()

            },
            onLongClickWord = {

            }
        )
        localBinding.wordRecyclerView.apply {
            adapter = recyclerWordAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // dummy 데이터
        var tempList = mutableListOf<Word>()
        ('a'..'z').forEach {
            val str = it.toString()
            tempList.add(Word(str,str,str,true))
        }
        recyclerWordAdapter.submitList(tempList)
    }


    private fun initSearchEditText(localBinding: FragmentWordsBinding) {
        localBinding.wordEditTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (localBinding.wordEditTextSearch.text.isNotEmpty()) {
                    //recyclerWordAdapter?.filter?.filter(charSequence)
                }
            }

            override fun afterTextChanged(charSequence: Editable?) {}

        })

    }




    // todo 다이얼로그 에서 삭제 ok 버튼 눌렀을 때의 로직


}