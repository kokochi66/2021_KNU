package com.capston.ocrwordbook.ui.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_NoTitleBar)
        super.onCreate(savedInstanceState)


         //DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_main)

        binding.mainLinearOcr.setOnClickListener {
            SwitchingOcr()
        }
        binding.mainLinearWords.setOnClickListener {
            SwitchingWords()
        }








    }

    fun SwitchingOcr() {
       binding.mainBarWords.visibility = View.INVISIBLE
       binding.mainImageWords.setImageResource(R.drawable.word_list_gray_700)
       binding.mainTextWords.setTextColor(getResources().getColor(R.color.gray_700, null))

       binding.mainBarOcr.visibility = View.VISIBLE
       binding.mainImageOcr.setImageResource(R.drawable.add_photo_white)
       binding.mainTextOcr.setTextColor(resources.getColor(R.color.white_text, null))
    }


    fun SwitchingWords() {
        binding.mainBarOcr.visibility = View.INVISIBLE
        binding.mainImageOcr.setImageResource(R.drawable.add_photo_gray_700)
        binding.mainTextOcr.setTextColor(getResources().getColor(R.color.gray_700, null))

        binding.mainBarWords.visibility = View.VISIBLE
        binding.mainImageWords.setImageResource(R.drawable.word_list_white)
        binding.mainTextWords.setTextColor(resources.getColor(R.color.white_text, null))


    }
}