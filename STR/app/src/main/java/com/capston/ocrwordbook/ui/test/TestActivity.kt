package com.capston.ocrwordbook.ui.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.data.Word
import com.capston.ocrwordbook.databinding.ActivityTestBinding

/**
 * 단어 테스트 화면
 * 단어가 최소 20개 이상 있어야 실행할 수 있는 기능이다.
 * 저장된 전체 단어에서 랜덤하게 20개를 가져오고, 각각 문제를 만들어서 화면에 보여준다.
 * 화면에 보이는 문제 형식은 영단어와 그에 맞는 뜻 1개와 틀린 뜻 4개, 20개 중에서 현재 몇번째 문제인지 보여주는 뷰이다.
 * 20개의 문제를 모두 풀면 몇개를 틀렸고 어느 단어를 틀렸는지 알려준다.
 *
 *
 * bindViews 버튼 ( 뒤로가기, 4지선다 문항에 리스너 붙이기 )
 * fetch20Words ( Word 전체를 가져오고 random 하게 20개를 뽑아 리스트를 반환한다. )
 * showEntireQuestion ( Word 20개 든 리스트를 인자로 받아 순회하며
 * isCorrectAnswer ( 유저가 답을 클릭했을 때 호출되어 답여부를 확인한다. )
 * addWrongWord ( 유저가 고른 답이 틀렸을 때 리스트에 추가한다. )
 * showNextQuestion ( 다음 문제를 가져온다. )
 * showResult ( 20 문제 다 풀었을 때 호출되어 결과를 보여준다. )
 */
class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestBinding
    lateinit var words: List<Word>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}