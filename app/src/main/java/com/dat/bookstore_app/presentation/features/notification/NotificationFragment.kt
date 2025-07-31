package com.dat.bookstore_app.presentation.features.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dat.bookstore_app.databinding.FragmentNotificationBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment


class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(inflater, container, false)
    }

    override fun setUpView() {

    }

    override fun observeViewModel() {
    }

}