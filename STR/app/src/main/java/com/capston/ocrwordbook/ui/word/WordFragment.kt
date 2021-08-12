package com.capston.ocrwordbook.ui.word

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.config.AppObject
import com.capston.ocrwordbook.data.Word
import com.capston.ocrwordbook.data.WordAppDatabase
import com.capston.ocrwordbook.databinding.FragmentWordsBinding
import com.capston.ocrwordbook.ui.web.WebActivity
import com.capston.ocrwordbook.ui.word.dialog.AddFolderDialog
import com.capston.ocrwordbook.ui.word.recylcer.WordRecyclerAdapter


/**
 * 저장한 단어를 리스트형태로 볼 수 있다.
 * 짧게 누르면 웹사전 화면으로 이동한다.
 * 길게 누르면 삭제 또는 폴더를 선택할 수 있게 해준다. ( 폴더 안의 폴더는 우선 계획 없음 )
 *
 * 단어인식결과화면 (ResultActivity) 에서 단어를 길게누르며 저장할 때 DB 에 단어를 저장한다.
 * 단어장화면이 만들어질 때마다 DB 에서 저장된 단어를 불러온다. ( 폴더 포함 )
 *      - 폴더는 word 자리에 폴더명을, meaning 자리에 빈문자열 ""을, favorite 자리에는 > 이미지를 설정하여 상단에 나타나게 한다.
 *      - 그리고 아이템 클릭이벤트에 단어면 WebActivity 로 이동하고, 폴더면 리스트아이템을 바꿔준다.
 *      - 리스트 순서는 위에서 아래로 폴더 -> favorite 단어 -> 단어
 * 단어장 화면에서 길게 누르면 삭제할 수 있게 만든다. ( 폴더 포함 )
 * TODO 프로젝트 전체 MVVM 패턴 제대로 적용
 * TODO 아래로 스크롤 하면 검색툴바가 사라지는 애니메이션 적용
 * TODO 단어와 폴더가 같은 데이터클래스로 처리되는데 데이터클래스 이름이 Word 인 것이 이상하니 다른 이름을 사용할것
 * TODO 폴더안에 폴더를 만들지 못하는 기능 추가, 폴더 뒤로가기 기능 추가
 * TODO 정렬
 */
class WordFragment : Fragment(R.layout.fragment_words) {
    private var binding: FragmentWordsBinding? = null
    private lateinit var recyclerWordAdapter: WordRecyclerAdapter
    private lateinit var db: WordAppDatabase
    private val handler = Handler(Looper.getMainLooper()) //메인스레드에 연결된 핸들러가 만들어진 것이다. TODO 스레드를 코루틴으로 처리하기
    private val wordComparator = Comparator<Word> { o1, o2 ->
        if(o1.isWord != o2.isWord) {
            o2.isWord.compareTo(o1.isWord)
        } else if(o1.favorite != o2.favorite) {
            o2.favorite.compareTo(o1.favorite)
        } else {
            o1.word.compareTo(o2.word)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentWordsBinding = FragmentWordsBinding.bind(view)
        binding = fragmentWordsBinding
        db = AppObject.getWordAppDatabase(requireContext())

        ('a'..'f').forEach {
            val str = it.toString()
            Thread {
                db.wordDao().insertWord(Word(str+"Main", str, str, false))
            }.start()
        }

        ('g'..'p').forEach {
            val str = it.toString()
            Thread {
                db.wordDao().insertWord(Word(str+"folderName", str, str, false, folderName = "folderName"))
            }.start()
        }


        initRecyclerView(fragmentWordsBinding)
        initSearchEditText(fragmentWordsBinding)
        initAddFolder(fragmentWordsBinding)
        getAndShowAllWordsFromDB()

    }

    private fun initRecyclerView(localBinding: FragmentWordsBinding) {
        recyclerWordAdapter = WordRecyclerAdapter(
            onClickWord = { word ->
               if (word.isWord) {
                   startWebActivity(word.word)
               } else {
                   getAndShowAllWordsFromDB(word.word) // 여기서 word.word 는 폴더명이다. 추후 이름을 수정할 계획이다.
               }
            },
            onLongClickWord = { word ->
                deleteWordFromDB(word)
            }
        )
        localBinding.wordRecyclerView.apply {
            adapter = recyclerWordAdapter
            layoutManager = LinearLayoutManager(context)
        }


    }


    private fun initSearchEditText(localBinding: FragmentWordsBinding) {
        localBinding.wordEditTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (localBinding.wordEditTextSearch.text.isNotEmpty()) {
                    recyclerWordAdapter?.filter?.filter(charSequence)
                } else {
                    recyclerWordAdapter.submitList(recyclerWordAdapter.currentList)
                }
            }

            override fun afterTextChanged(charSequence: Editable?) {}

        })
    }

    private fun initAddFolder(localBinding: FragmentWordsBinding) {
        localBinding.wordImageButtonAddFolder.setOnClickListener {
            showAddFolderDialog()
        }
    }
    private fun showAddFolderDialog() {
        AddFolderDialog(requireContext()) { folderName ->
            saveFolderToDB(Word(folderName, folderName, "", favorite = false, isWord = false))
        }.show()
    }
    private fun initTestButton(localBinding: FragmentWordsBinding) {
        // TODO 테스트 기능 추가

    }

    private fun getAndShowAllWordsFromDB(folderName: String = "Main") {
        Thread {
            val words = db.wordDao().getAllWords(folderName)
            handler.post {
                showAllWordsToRecyclerView(words)
            }
        }.start()
    }


    private fun showAllWordsToRecyclerView(words: List<Word>) {
        recyclerWordAdapter.submitList(words.sortedWith(wordComparator))
    }


    private fun deleteWordFromDB(word: Word) {
        Thread {
            db.wordDao().deleteWord(word)
            handler.post {
                getAndShowAllWordsFromDB()
                Toast.makeText(context, "단어가 삭제되었습니다.", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun saveFolderToDB(word: Word) {
        Thread {
            db.wordDao().insertWord(word)
            handler.post {
                getAndShowAllWordsFromDB()
            }
        }.start()

    }

    private fun startWebActivity(word: String) {
        val intent = Intent(requireContext(), WebActivity::class.java)
        intent.putExtra("word", word)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}