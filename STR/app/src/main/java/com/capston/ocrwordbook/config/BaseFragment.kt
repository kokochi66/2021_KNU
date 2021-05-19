package com.capston.ocrwordbook.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel


abstract class BaseFragment<B : ViewDataBinding, VM : ViewModel>(private val layoutId: Int) : Fragment() {

    // Data Binding
    lateinit var binding: B

    // View Model
    abstract var viewModel: VM


    // Loading Dialog
    //private var mLoadingDialog: LoadingDialog? = context?.let { LoadingDialog(it) }

    // View Model 설정
    abstract fun setViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // DataBinding 설정
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        // View Model 설정
        setViewModel()


        // Show Toast
        fun showToast(resId: Int) {
            context?.let { Toast.makeText(it, it.getString(resId), Toast.LENGTH_SHORT).show() }
        }

        return binding.root


    }
}
