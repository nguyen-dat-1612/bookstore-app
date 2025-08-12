package com.dat.bookstore_app.presentation.features.home

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.*
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentHomeBinding
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.Banner
import com.dat.bookstore_app.presentation.common.adapter.BannerAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.main.BottomNavFragmentDirections
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    private val tabPopularItems = listOf(
        Pair(Sort.SOLD_DESC, "Phổ biến"),
        Pair(Sort.NEW_DESC, "Mới nhất")
    )

    private val tabPriceItems = listOf(
        Pair(Sort.PRICE_ASC, "Giá tăng dần"),
        Pair(Sort.PRICE_DESC, "Giá giảm dần")
    )
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }


    private val mockBanners = listOf(
        Banner("1", "https://cdn1.fahasa.com/media/magentothem/banner7/MCbooks_Vang_T7_Resize_840x320_1.png"),
        Banner("2", "https://cdn1.fahasa.com/media/magentothem/banner7/MCbooks_Vang_T7_Resize_840x320_1.png"),
        Banner("3", "https://cdn1.fahasa.com/media/magentothem/banner7/MCbooks_Vang_T7_Resize_840x320_1.png")
    )

    private var scrollChangedListener: OnScrollChangedListener? = null

    private var isBannerInitialized = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        toggleShimmer(true)

        setUpViewPager()
        setUpScroll()
        setUpOnListener()
    }

    private fun setUpViewPager() {
        binding.apply {
            setupViewPager(vpPopular, tabLayoutPopular, tabPopularItems) { sort ->
                BookListPagerFragment.newInstance(sort)
            }

            setupViewPager(vpPriceBooks, tabLayoutPrice, tabPriceItems) { sort ->
                BookListPagerFragment.newInstance(sort)
            }
        }
    }

    private fun setUpScroll() {
        binding.apply {
            // Search click
            header.translationY = dpToPx(48f).toFloat()
            val localBinding = binding
            scrollChangedListener = OnScrollChangedListener {
                if (!isAdded || view == null) return@OnScrollChangedListener

                val scrollY = localBinding.scrollView.scrollY

                val location = IntArray(2)
                localBinding.headerPlaceholder.getLocationOnScreen(location)
                val placeholderY = location[1]

                val rootLocation = IntArray(2)
                localBinding.root.getLocationOnScreen(rootLocation)
                val rootTopY = rootLocation[1]

                if (placeholderY <= rootTopY) {
                    localBinding.header.translationY = 0f
                    localBinding.header.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
                } else {
                    val offset = placeholderY - rootTopY
                    localBinding.header.translationY = offset.toFloat()
                    localBinding.header.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            scrollView.viewTreeObserver.addOnScrollChangedListener(scrollChangedListener)
        }
    }

    private fun setUpOnListener() {
        binding.apply {
            layoutSearch.setOnClickListener {
                navController.navigate(BottomNavFragmentDirections.actionBottomNavFragmentToSearchInputFragment(null))
            }

            btnSeeMorePopular.setOnClickListener {
                navController.navigate(R.id.action_bottomNavFragment_to_popularFragment)
            }
            btnSeeMorePrice.setOnClickListener {
                navController.navigate(R.id.action_bottomNavFragment_to_newFragment)
            }

            swipeRefreshLayout.setOnRefreshListener {
                setUpViewPager()
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
        ).toInt()
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    binding.apply {
                        if (!isBannerInitialized) {
                            vpBanner.adapter = BannerAdapter(mockBanners)
                            vpBanner.offscreenPageLimit = 1

                            startAutoScrollBanner()
                            isBannerInitialized = true
                        }

                        toggleShimmer(false)

                    }
                }
            }
        }
    }
    private fun startAutoScrollBanner() {
        currentBannerPosition = 0

        bannerRunnable = object : Runnable {
            override fun run() {
                if (mockBanners.isEmpty()) return

                val nextPosition = currentBannerPosition + 1

                if (nextPosition >= mockBanners.size) {
                    // Về đầu không animation
                    binding.vpBanner.setCurrentItem(0, false)
                    currentBannerPosition = 0

                    // Delay nhỏ để tránh nháy quá nhanh
                    bannerHandler.postDelayed(this, bannerInterval)
                } else {
                    binding.vpBanner.setCurrentItem(nextPosition, true)
                    currentBannerPosition = nextPosition
                    bannerHandler.postDelayed(this, bannerInterval)
                }
            }
        }

        bannerHandler.postDelayed(bannerRunnable!!, bannerInterval)
    }


    override fun onDestroyView() {
        scrollChangedListener?.let {
            if (binding.scrollView.viewTreeObserver.isAlive) {
                binding.scrollView.viewTreeObserver.removeOnScrollChangedListener(it)
            }
        }
        scrollChangedListener = null
        bannerRunnable?.let {
            bannerHandler.removeCallbacks(it)
            bannerRunnable = null
        }
        bannerRunnable = null
        isBannerInitialized = false
        super.onDestroyView()
    }

    private fun toggleShimmer(isLoading: Boolean) = with(binding) {
        if (isLoading) {
            // Start shimmer
            shimmerBanner.shimmerBanner.startShimmer()
            shimmerPopular.shimmerBooks.startShimmer()
            shimmerPrice.shimmerBooks.startShimmer()

            shimmerBanner.shimmerBanner.visibility = View.VISIBLE
            shimmerPopular.shimmerBooks.visibility = View.VISIBLE
            shimmerPrice.shimmerBooks.visibility = View.VISIBLE

            vpBanner.visibility = View.INVISIBLE
        } else {
            // Stop shimmer
            Handler(Looper.getMainLooper()).postDelayed({
                shimmerBanner.shimmerBanner.stopShimmer()
                shimmerPopular.shimmerBooks.stopShimmer()
                shimmerPrice.shimmerBooks.stopShimmer()

                shimmerBanner.shimmerBanner.visibility = View.GONE
                shimmerPopular.shimmerBooks.visibility = View.GONE
                shimmerPrice.shimmerBooks.visibility = View.GONE

                vpBanner.visibility = View.VISIBLE
            }, 200)
        }
    }

    private fun setupViewPager(
        viewPager: ViewPager2,
        tabLayout: TabLayout,
        tabItems: List<Pair<Sort, String>>,
        fragmentCreator: (Sort) -> Fragment
    ) {
        viewPager.apply {
            adapter = object : FragmentStateAdapter(this@HomeFragment) {
                override fun getItemCount() = tabItems.size
                override fun createFragment(position: Int) = fragmentCreator(tabItems[position].first)
            }
            isUserInputEnabled = false
            offscreenPageLimit = tabItems.size
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabItems[position].second
        }.attach()
    }

    companion object {
        private val bannerHandler = Handler(Looper.getMainLooper())
        private var bannerRunnable: Runnable? = null
        private var currentBannerPosition = 0
        private val bannerInterval = 3000L // mỗi 3 giây đổi banner
    }
}

