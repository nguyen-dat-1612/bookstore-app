package com.dat.bookstore_app.presentation.features.purchase_history

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dat.bookstore_app.domain.enums.OrderStatus

class PurchaseHistoryPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val tabs = OrderStatus.values()

    override fun getItemCount(): Int = tabs.size

    override fun createFragment(position: Int): Fragment {
        return OrderListFragment.newInstance(tabs[position])
    }
}