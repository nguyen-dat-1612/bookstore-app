package com.dat.bookstore_app.presentation.features.book

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dat.bookstore_app.databinding.FragmentProductInformationBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment

class ProductInformationFragment : BaseFragment<FragmentProductInformationBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProductInformationBinding {
        return FragmentProductInformationBinding.inflate(inflater, container, false)
    }

    override fun setUpView() {

    }

    override fun observeViewModel() {

    }

}