package com.capston.ocrwordbook.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capston.ocrwordbook.R
import com.capston.ocrwordbook.config.BaseFragment
import com.capston.ocrwordbook.databinding.FragmentCameraBinding
import com.capston.ocrwordbook.databinding.FragmentResultBinding
import com.capston.ocrwordbook.ui.camera.CameraViewModel

class ResultFragment : BaseFragment<FragmentResultBinding, ResultViewModel>(R.layout.fragment_result) {

    override var viewModel: ResultViewModel = ResultViewModel()
    override fun setViewModel() {
        binding.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        //옵저버로 바뀐 것을 알 수있다. 생명주시와 상관 없는 모든 작업은 여기서 해도 무방



        return binding.root
    }



}