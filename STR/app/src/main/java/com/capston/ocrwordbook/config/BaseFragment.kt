package com.capston.ocrwordbook.config

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.capston.ocrwordbook.databinding.ActivityMainBinding.bind
import com.capston.ocrwordbook.utils.LoadingDialog


abstract class BaseFragment<B : ViewDataBinding, VM : ViewModel>(private val layoutId: Int) : Fragment(layoutId) {

    lateinit var mLoadingDialog: LoadingDialog

    // Data Binding
    lateinit var binding: B

    // View Model
    abstract var viewModel: VM


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun showLoadingDialog(context: Context) {
        mLoadingDialog = LoadingDialog(context)
        mLoadingDialog.show()
    }

    fun dismissLoadingDialog() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
    }
}
