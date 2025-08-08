package com.dat.bookstore_app.presentation.features.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentBottomNavBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment

class BottomNavFragment : BaseFragment<FragmentBottomNavBinding>() {

    private val sharedViewModel: MainSharedViewModel by activityViewModels()
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBottomNavBinding {
        return FragmentBottomNavBinding.inflate(inflater, container, false)
    }

    override fun setUpView() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_bottom) as NavHostFragment
        val navController = navHostFragment.navController

        // Đồng bộ BottomNav với NavController con
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun observeViewModel() {
        sharedViewModel.tabToSwitch.observe(viewLifecycleOwner) { tab ->
            val itemId = when (tab) {
                "home" -> R.id.homeFragment
                "cart" -> R.id.cartFragment
                "account" -> R.id.accountFragment
                "notification" -> R.id.notificationFragment
                else -> null
            }
            itemId?.let { binding.bottomNav.selectedItemId = it }
        }
    }
}