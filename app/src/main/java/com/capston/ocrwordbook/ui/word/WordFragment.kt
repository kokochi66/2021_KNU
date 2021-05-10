package com.capston.ocrwordbook.ui.word

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.config.BaseFragment
import com.capston.ocrwordbook.databinding.FragmentWordBinding
import com.capston.ocrwordbook.ui.gallery.GalleryViewModel

class WordFragment : BaseFragment<FragmentWordBinding, WordViewModel>(R.layout.fragment_word) {

    override var viewModel: WordViewModel = WordViewModel()
    override fun setViewModel() {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)



        return binding.root
    }

}