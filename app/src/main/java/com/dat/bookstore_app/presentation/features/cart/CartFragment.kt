package com.dat.bookstore_app.presentation.features.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.dat.bookstore_app.databinding.FragmentCartBinding
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.presentation.common.adapter.CartAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.presentation.features.main.BottomNavFragmentDirections
import com.dat.bookstore_app.utils.helpers.CurrencyUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.dat.bookstore_app.R
import com.dat.bookstore_app.presentation.features.main.MainSharedViewModel
import com.dat.bookstore_app.presentation.features.main.MainViewModel
import com.dat.bookstore_app.utils.extension.hide
import com.dat.bookstore_app.utils.extension.show

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>() {

    private val cartViewModel: CartViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val sharedViewModel: MainSharedViewModel by activityViewModels()

    private var hasLoadedCart = false
    private var isAllSelected = false

    private val adapter by lazy {
        CartAdapter(
            onUpdateClick = { item, quantity ->
                cartViewModel.updateCart(item.book.id, quantity)
            },
            onDeleteClick = { item->
                cartViewModel.deleteFromCart(item.book.id)
            },
            onCheckedChange = { item, isChecked ->
                cartViewModel.updateChecked(item.book.id, isChecked)
            },
            onItemClicked = { item ->
                val action = BottomNavFragmentDirections
                    .actionBottomNavFragmentToDetailBookFragment(item.book)
                navController.navigate(action)
            }
        )
    }

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCartBinding {
        return FragmentCartBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding){
        rvCart.adapter = adapter

        // Kiểm tra login ngay lúc setup view
        if (!mainViewModel.uiState.value.isLoggedIn) {
            showRequireLoginLayout()
        } else {
            cartViewModel.loadCart()
            hasLoadedCart = true
        }
        btnPayment.setOnClickListener {
            val selectedCarts = cartViewModel.uiState.value.ListCart.filter { it.isSelected }
            if (selectedCarts.isEmpty()) {
                showToast("Vui lòng chọn sản phẩm trước khi thanh toán")
                return@setOnClickListener
            }
            val action = BottomNavFragmentDirections
                .actionBottomNavFragmentToPaymentFragment(
                    cartList = selectedCarts.toTypedArray()
                )
            navController.navigate(action)
        }
        layoutSelectAll.setOnClickListener {
            isAllSelected = !isAllSelected

            val iconRes = if (isAllSelected)
                R.drawable.ic_checkbox_checked
            else
                R.drawable.ic_checkbox_unchecked

            imgCheckbox.setImageResource(iconRes)
            cartViewModel.updateAllChecked(isAllSelected)
        }

        btnBuyNow.setOnClickListener {
            sharedViewModel.switchTab("home")

        }
        btnLogin.setOnClickListener {
            sharedViewModel.switchTab("account")
        }
    }

    override fun observeViewModel() {
        // 1. Quan sát login state
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiState.collectLatest { mainState ->
                    if (!mainState.isLoggedIn) {
                        showRequireLoginLayout()
                        hasLoadedCart = false
                    } else if (!hasLoadedCart) {
                        cartViewModel.loadCart()
                        hasLoadedCart = true
                    }
                }
            }
        }

        // 2. Quan sát cart state
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.uiState.collectLatest { cartState ->
                    val isLoggedIn = mainViewModel.uiState.value.isLoggedIn
                    if (!isLoggedIn) return@collectLatest

                    val list = cartState.ListCart
                    if (list.isEmpty()) {
                        showEmptyCartLayout()
                    } else {
                        showCartContentLayout()
                        adapter.submitList(list)
                        binding.tvSelectAll.text = "Chọn tất cả (${list.size} sản phẩm)"
                        setUpPriceTotal(list)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.errorsState.errors.collect {
                    showToast(it.message.toString())
                }
            }
        }
    }
    private fun showRequireLoginLayout() = with(binding) {
        layoutLoginRequest.show()
        layoutEmptyCart.hide()
        rvCart.hide()
        bottomPayment.hide()
    }

    private fun showEmptyCartLayout() = with(binding) {
        layoutLoginRequest.hide()
        layoutEmptyCart.show()
        rvCart.hide()
        bottomPayment.hide()
    }

    private fun showCartContentLayout() = with(binding) {
        layoutLoginRequest.hide()
        layoutEmptyCart.hide()
        rvCart.show()
        bottomPayment.show()
    }

    private fun setUpPriceTotal(listCart: List<Cart>) = with(binding) {
        val priceTotal = listCart
            .filter { it.isSelected }
            .sumOf { (it.book.price.toInt() * it.quantity) * (100 - it.book.discount) / 100 }
        tvPricePayment.text = CurrencyUtils.formatVND(priceTotal)
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
}