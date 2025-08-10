package com.dat.bookstore_app.presentation.features.popular

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentNewBinding
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewFragment : BaseFragment<FragmentNewBinding>() {

    private val priceTabs = listOf("Giá tăng dần", "Giá giảm dần")

    // parent fragment không cần viewmodel cho list (child sẽ có ViewModel riêng)
    private val tabItems = listOf(
        Pair(Sort.PRICE_ASC, "Giá tăng dần"),
        Pair(Sort.PRICE_DESC, "Giá giảm dần")
    )
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewBinding {
        return FragmentNewBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        viewPagerPrice.adapter = BookPagerAdapter(this@NewFragment)

        // giữ các fragment con trong bộ nhớ để không reload khi đổi tab
        viewPagerPrice.offscreenPageLimit = tabItems.size

        // nối TabLayout với ViewPager2
        TabLayoutMediator(tabLayoutPrice, viewPagerPrice) { tab, position ->
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
        // Không dùng ở đây, collect trực tiếp khi chọn tab
    }
    private inner class BookPagerAdapter(fragment: NewFragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = tabItems.size

        override fun createFragment(position: Int) =
            BookListTabFragment.newInstance(
                sort = tabItems[position].first,
                source = "New"
            )
    }
}
