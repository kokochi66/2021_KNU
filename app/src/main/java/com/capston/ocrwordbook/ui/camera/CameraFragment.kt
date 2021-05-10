package com.capston.ocrwordbook.ui.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.config.BaseFragment
import com.capston.ocrwordbook.databinding.FragmentCameraBinding

class CameraFragment : BaseFragment<FragmentCameraBinding, CameraViewModel>(R.layout.fragment_camera) {

    override var viewModel: CameraViewModel = CameraViewModel()
    override fun setViewModel() {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)



        return binding.root
    }



}