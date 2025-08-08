package com.dat.bookstore_app.presentation.features.purchase_history

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentDetailOrderBinding
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.presentation.common.adapter.OrderDetailAdapter
import com.dat.bookstore_app.presentation.common.adapter.StepAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.utils.extension.hide
import com.dat.bookstore_app.utils.helpers.CurrencyUtils
import com.dat.bookstore_app.utils.helpers.DateHelper
import com.dat.bookstore_app.utils.extension.show
import com.dat.bookstore_app.utils.extension.mapSteps
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailOrderFragment : BaseFragment<FragmentDetailOrderBinding>() {

    private val viewModel: DetailOrderViewModel by viewModels()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }
    private val stepAdapter by lazy {
        StepAdapter()
    }
    private val detailAdapter by lazy {
        OrderDetailAdapter(
            onItemClicked = {

            }
        )
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailOrderBinding {
        return FragmentDetailOrderBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding){
        rvStep.adapter = stepAdapter
        btnBack.setOnClickListener {
            navController.popBackStack()
        }
        rvDetailOrder.adapter = detailAdapter
        rvDetailOrder.layoutManager = LinearLayoutManager(requireContext())
        btnBuyAgain.setOnClickListener {
            val buyAgainList = viewModel.uiState.value.order!!.orderItems.map {
                Cart(
                    id = it.book.id,
                    quantity = it.quantity,
                    createdAt = "",
                    updatedAt = "",
                    book = it.book,
                    isSelected = true
                )
            }.toTypedArray()

            val action = PurchaseHistoryFragmentDirections
                .actionPurchaseHistoryFragmentToPaymentFragment( cartList = buyAgainList)
            binding.root.findNavController().navigate(action)
        }
        btnRetryPayment.setOnClickListener{
            navController.navigate(
                PurchaseHistoryFragmentDirections.actionPurchaseHistoryFragmentToRetryPaymentFragment(
                    viewModel.uiState.value.order!!.id
                )
            )
        }
        btnCancelOrder.setOnClickListener {
            viewModel.cancelOrder()
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    state.order?.let { order ->
                        renderOrderInfo(order)
                        renderReceiverInfo(order)
                        renderSteps(order)
                        renderButtons(order)
                        renderPaymentIfAny(state)
                    }
                    if (state.canCancelOrder) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    private fun renderOrderInfo(order: Order) = with(binding.includeOrderInfo) {
        tvOrderId.text = "#${order.id}"
        tvOrderDate.text = DateHelper.formatOrderDate(order.createdAt)
        tvOrderQuantity.text = "${order.orderItems.size} sản phẩm"
        tvOrderTotal.text = CurrencyUtils.formatVND(order.totalAmount)

        binding.tvStatus.text = order.status.title
        binding.tvPriceOrder.text = CurrencyUtils.formatVND(order.totalAmount)
    }

    private fun renderReceiverInfo(order: Order) = with(binding.includeReceiverInfo) {
        tvReceiverName.text = order.fullName
        tvReceiverPhone.text = order.phone
        tvReceiverAddress.text = order.shippingAddress
    }

    private fun renderSteps(order: Order) {
        val steps = mapSteps(order.status, order.createdAt, order.updatedAt)
        stepAdapter.submitList(steps)
        detailAdapter.submitList(order.orderItems)
    }

    private fun renderButtons(order: Order) = with(binding) {
        btnRetryPayment.hide()
        btnCancelOrder.hide()
        btnBuyAgain.hide()
        footerContainer.show()

        when (order.status) {
            OrderStatus.PENDING -> {
                if (order.paymentMethod != PaymentMethod.COD) {
                    btnRetryPayment.show()
                    btnRetryPayment.setOnClickListener { viewModel.createPayment() }
                }

                btnCancelOrder.show()
                btnCancelOrder.setOnClickListener { viewModel.cancelOrder() }
            }

            OrderStatus.DELIVERED, OrderStatus.CANCELLED -> {
                btnBuyAgain.show()
                btnBuyAgain.setOnClickListener {

                }
            }

            OrderStatus.ALL, OrderStatus.SHIPPING, OrderStatus.CONFIRMED -> {
                footerContainer.hide()
            }
        }
    }

    private fun renderPaymentIfAny(state: DetailOrderUiState) {
        state.payment?.let { payment ->
            // viewModel.clearPayment() // nếu bạn cần ngăn trigger lại
            openUrlInChrome(requireContext(), payment.paymentUrl)
        }
    }

    private fun openUrlInChrome(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.android.chrome")
        }

        // Nếu Chrome không có thì mở bằng trình duyệt mặc định
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(fallbackIntent)
        }
    }

}