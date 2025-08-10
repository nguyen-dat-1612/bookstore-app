package com.dat.bookstore_app.presentation.features.auth

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dat.bookstore_app.databinding.FragmentForgotBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotFragment : BaseFragment<FragmentForgotBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotBinding {
        return FragmentForgotBinding.inflate(inflater, container, false)
    }

    override fun setUpView() {

    }

    override fun observeViewModel() {

    }

}