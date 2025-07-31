package com.dat.bookstore_app.presentation.features.account

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dat.bookstore_app.databinding.FragmentAuthTabBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.login.LoginFragment
import com.dat.bookstore_app.presentation.features.register.RegisterFragment

class AuthTabFragment : BaseFragment<FragmentAuthTabBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAuthTabBinding {
        return FragmentAuthTabBinding.inflate(inflater, container, false)
    }

    override fun setUpView() {
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        val fragmentList = listOf(LoginFragment(), RegisterFragment())
        val titleList = listOf("Đăng nhập", "Đăng ký")

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titleList[position]
        }.attach()

    }

    override fun observeViewModel() {

    }
}