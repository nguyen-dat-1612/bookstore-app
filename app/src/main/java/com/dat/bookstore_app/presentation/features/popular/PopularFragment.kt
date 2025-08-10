package com.dat.bookstore_app.presentation.features.popular

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentPopularBinding
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularFragment : BaseFragment<FragmentPopularBinding>() {

    // parent fragment không cần viewmodel cho list (child sẽ có ViewModel riêng)
    private val tabItems = listOf(
        Pair(Sort.SOLD_DESC, "Phổ biến"),
        Pair(Sort.NEW_DESC, "Mới nhất")
    )

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPopularBinding {
        return FragmentPopularBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        // set adapter cho ViewPager2 - truyền this (fragment) để FragmentStateAdapter dùng childFragmentManager
        viewPagerPopular.adapter = BookPagerAdapter(this@PopularFragment)

        // giữ các fragment con trong bộ nhớ để không reload khi đổi tab
        viewPagerPopular.offscreenPageLimit = tabItems.size

        // nối TabLayout với ViewPager2
        TabLayoutMediator(tabLayoutPopular, viewPagerPopular) { tab, position ->
            tab.text = tabItems[position].second
        }.attach()

        // back / search
        btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
        layoutSearch.setOnClickListener {
            // điều hướng như trước
            navController.navigate(PopularFragmentDirections.actionPopularFragmentToSearchInputFragment(null))
        }
    }

    override fun observeViewModel() {
        // nothing here: children observe their own flows
    }

    private inner class BookPagerAdapter(fragment: PopularFragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = tabItems.size

        override fun createFragment(position: Int) =
            BookListTabFragment.newInstance(
                sort = tabItems[position].first,
                source = "Popular"
            )
    }
}
