package com.dat.bookstore_app.presentation.features.purchase_history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentPurchaseHistoryBinding
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PurchaseHistoryFragment : BaseFragment<FragmentPurchaseHistoryBinding>() {

    private lateinit var adapter: PurchaseHistoryPagerAdapter

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPurchaseHistoryBinding {
        return FragmentPurchaseHistoryBinding.inflate(inflater, container, false)
    }

    override fun setUpView() {
        binding.apply {
            btnBack.setOnClickListener {
                navController.popBackStack()
            }

            lifecycleScope.launch {
                delay(250) // delay để tránh đơ UI lúc load fragment con
                adapter = PurchaseHistoryPagerAdapter(this@PurchaseHistoryFragment)
                viewPager.adapter = adapter

                val selectedStatus = arguments?.getString(ARG_ORDER_STATUS)?.let {
                    OrderStatus.valueOf(it)
                } ?: OrderStatus.ALL

                val initialTabIndex = OrderStatus.values().indexOf(selectedStatus)
                viewPager.setCurrentItem(initialTabIndex, false)

                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = OrderStatus.values()[position].title
                }.attach()
            }
        }

    }

    override fun observeViewModel() {

    }

    companion object {
        const val ARG_ORDER_STATUS = "arg_order_status"
    }
}