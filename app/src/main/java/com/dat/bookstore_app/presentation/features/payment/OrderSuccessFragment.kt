package com.dat.bookstore_app.presentation.features.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentOrderSuccessBinding
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.utils.extension.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderSuccessFragment : BaseFragment<FragmentOrderSuccessBinding>() {

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val viewModel: OrderSuccessViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOrderSuccessBinding {
        return FragmentOrderSuccessBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        backToHomeBtn.setOnClickListener {
            navController.navigate(R.id.action_orderSuccessFragment_to_bottomNavFragment)
        }
        viewModel.loadOrder()
    }


    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    state.order?.let { order ->
                        setUpStatus(order)
                    }
                }
            }

        }
    }

    private fun setUpStatus(order: Order) = with(binding) {
        if (order.status == OrderStatus.PENDING) {
            resultTitle.text = "Đơn hàng #${order.id} đã được đặt thành công!"
            resultDescription.text = "Đơn hàng đang chờ thanh toán. Vui lòng thanh toán trong vòng 24h."
            backToHomeBtn.show()
        }
        if (order.status == OrderStatus.CONFIRMED) {

            resultTitle.text = "Đơn hàng  #${order.id} đã được thanh toán!"
            resultDescription.text = "Đơn hàng của bạn đang được chuẩn bị và sẽ sớm được giao đúng hạn."
            backToHomeBtn.show()
        }
        if (order.status == OrderStatus.CANCELLED) {
            successAnimationView.setAnimation(R.raw.error_animation)
            resultTitle.text = "Đơn hàng #${order.id} đã bị hủy!"
            resultDescription.text = "Đơn hàng của bạn đã bị hủy. Vui lòng đặt lại đơn hàng."
            backToHomeBtn.show()
        }
    }
}