package com.capston.ocrwordbook.ui.gallery

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.config.BaseFragment
import com.capston.ocrwordbook.databinding.FragmentGalleryBinding
import com.capston.ocrwordbook.ui.camera.CameraViewModel

class GalleryFragment : BaseFragment<FragmentGalleryBinding, GalleryViewModel>(R.layout.fragment_gallery) {

    override var viewModel: GalleryViewModel = GalleryViewModel()
    override fun setViewModel() {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)



        return binding.root
    }



}