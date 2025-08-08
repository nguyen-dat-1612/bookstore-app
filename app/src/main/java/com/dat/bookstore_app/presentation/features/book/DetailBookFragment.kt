package com.dat.bookstore_app.presentation.features.book

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentDetailBookBinding
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.presentation.common.adapter.ImagePagerAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.main.MainSharedViewModel
import com.dat.bookstore_app.utils.extension.setDiscountedPrice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailBookFragment : BaseFragment<FragmentDetailBookBinding>() {

    private val viewModel: DetailBookViewModel by viewModels()
    private val sharedViewModel: MainSharedViewModel by activityViewModels()

    private val imagePagerAdapter: ImagePagerAdapter by lazy {
        ImagePagerAdapter(emptyList())
    }
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailBookBinding {
        return FragmentDetailBookBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        btnIncrease.setOnClickListener {
            viewModel.increase()
        }
        btnDecrease.setOnClickListener {
            viewModel.decrease()
        }
        appbarProductDetail.btnBack.setOnClickListener {
            navController.popBackStack()
        }
        btnAddToCart.setOnClickListener {
            viewModel.addToCart()
        }
        btnSeeMore.root.setOnClickListener {
            navController.navigate(DetailBookFragmentDirections.actionDetailBookFragmentToBookInformationFragment(viewModel.book))
        }
        appbarProductDetail.btnCart.setOnClickListener {
            navigateTabBottomNav("cart")
        }
        appbarProductDetail.btnHome.setOnClickListener {
            navigateTabBottomNav("home")
        }
        appbarProductDetail.btnSearch.setOnClickListener {
            navController.navigate(R.id.action_detailBookFragment_to_searchInputFragment)
        }
        btnBuyNow.setOnClickListener {
            val listCart = listOf(Cart(
                id = 1,
                createdAt = "",
                updatedAt = "",
                quantity = viewModel.uiState.value.count,
                isSelected = true,
                book = viewModel.uiState.value.book!!
            ));
            navController.navigate(DetailBookFragmentDirections.actionDetailBookFragmentToPaymentFragment(listCart.toTypedArray()))
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val total = viewModel.uiState.value.book?.slider?.size // hoặc list ảnh bạn truyền vào adapter
                binding.tvImageCounter.text = "${position + 1}/$total"
            }
        })
        btnFavorite.setOnClickListener {
            if (viewModel.uiState.value.isFavorite) {
                viewModel.deleteFavorite()
            } else {
                viewModel.addToFavorite()
            }
        }

    }
    
    fun navigateTabBottomNav(tab: String) {

        val options = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .setPopUpTo(R.id.detailBookFragment, true)
            .setLaunchSingleTop(true)
            .build()
        sharedViewModel.switchTab(tab)
        navController.navigate(R.id.bottomNavFragment, null, options)
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    if (it.book != null) {
                        setUpBook(book = it.book)
                    }
                    if (it.count != null) {
                        binding.tvCount.text = it.count.toString()
                    }
                    if (it.addCartSuccess) {
                        showToast("Thêm vào giỏ hàng thành công")
                    }
                    if (it.addFavoriteSuccess) {
                        binding.btnFavorite.setImageResource(R.drawable.ic_favorite_fill)
                        showToast("Thêm vào danh sách yêu thích thành công")
                    }
                    if (it.isFavorite) {
                        binding.btnFavorite.setImageResource(R.drawable.ic_favorite_fill)
                    } else {
                        binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
                    }

                }
            }
        }

    }

    private fun setUpBook(book : Book) = with(binding) {
        viewPager.adapter = ImagePagerAdapter(book.slider)
        tvNameBook.text = book.title
        tvPriceBook.setDiscountedPrice(book.price.toInt(), book.discount)
        tvDiscountPercent.text = getString(R.string.discount_percent, book.discount)
        tvSold.text = getString(R.string.sold, book.sold)

        // Thông tin sản phẩm
        tvIdBook.text = book.id.toString()
        tvAge.text = getString(R.string.age, book.age)
        tvAuthor.text = book.author
        tvPublisher.text = book.publisher
        tvDescription.text = book.description
    }

}