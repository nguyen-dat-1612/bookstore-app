package com.dat.bookstore_app.presentation.features.popular

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentPopularBinding
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    override fun setUpView() {
        binding.apply {
            btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
            layoutSearch.setOnClickListener {
                // điều hướng như trước
                navController.navigate(PopularFragmentDirections.actionPopularFragmentToSearchInputFragment(null))
            }

            lifecycleScope.launch {
                delay(250) // hoặc ít hơn 50ms
                viewPagerPopular.adapter = BookPagerAdapter(this@PopularFragment)
                viewPagerPopular.offscreenPageLimit = tabItems.size
                TabLayoutMediator(tabLayoutPopular, viewPagerPopular) { tab, position ->
                    tab.text = tabItems[position].second
                }.attach()
            }
        }

    }

    override fun observeViewModel() {

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
