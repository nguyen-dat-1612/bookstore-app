package com.dat.bookstore_app.presentation.features.payment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.FragmentRetryPaymentBinding
import com.dat.bookstore_app.domain.enums.OrderFlowState
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.presentation.common.adapter.OrderDetailAdapter
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import com.dat.bookstore_app.utils.helpers.CurrencyUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RetryPaymentFragment : BaseFragment<FragmentRetryPaymentBinding>() {

    private val viewModel: RetryPaymentViewModel by viewModels()

    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }

    private val adapter by lazy {
        OrderDetailAdapter(
            onItemClicked = {
                navController.navigate(
                    RetryPaymentFragmentDirections.actionRetryPaymentFragmentToDetailBookFragment(it.book)
                )
            }
        )
    }
    private var deeplinkHandled = false
    private var fallbackHandled = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRetryPaymentBinding {
        return FragmentRetryPaymentBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        rvBookPayment.adapter = adapter

        btnOrder.setOnClickListener {
            viewModel.createPayment()
        }
        btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loadingState.loading.collect {
                        binding.layoutViewProgress.root.visibility = if (it) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.uiState.collectLatest {
                        if (it.order != null) {
                            setUpUi(it.order);
                        }

                        if (it.payment != null && it.orderFlowState == OrderFlowState.ORDER_CREATED_VNPAY_REDIRECTED) {
//                        openUrlInChrome(requireActivity(), it.payment.paymentUrl)
                            openPaymentInCustomTab(it.payment.paymentUrl)
                        }

                        val orderId = it.order?.id
                        val flowState = it.orderFlowState

                        val shouldNavigate = when (flowState) {
                            OrderFlowState.ORDER_VNPAY_SUCCESS -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Thanh toán thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                true
                            }

                            OrderFlowState.ORDER_VNPAY_FAILED,
                            OrderFlowState.ORDER_VNPAY_PENDING,
                            OrderFlowState.ORDER_VNPAY_EMPTY_RESULT -> {
                                Log.d("RetryPaymentFragment", "countPayment: ${it.countPayment}")
                                false
                            }

                            OrderFlowState.ORDER_CANCELLED -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Đơn hàng đã bị hủy",
                                    Toast.LENGTH_LONG
                                ).show()
                                viewModel.cancelOrder()
                                true
                            }

                            else -> false
                        }

                        if (shouldNavigate && orderId != null) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate(
                                    RetryPaymentFragmentDirections.actionRetryPaymentFragmentToOrderSuccessFragment(
                                        orderId
                                    )
                                )
                            }, 2000) // 🕒 Delay 2s
                        }
                    }
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("RetryPaymentFragment", "onStart")
        viewModel.updatePayment()
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Bundle>("deep_link_result")
            ?.observe(viewLifecycleOwner) { bundle ->
                deeplinkHandled = true

                val transactionId = bundle.getString("transactionId")
                if (transactionId != null) {
                    viewModel.checkTransactionStatus(transactionId)
                } else {
                    viewModel.handleEmptyTransactionId()
                }
                navController.currentBackStackEntry?.savedStateHandle?.remove<Bundle>("deep_link_result")
            }
    }

    override fun onResume() {
        super.onResume()
        Log.d("RetryPaymentFragment", "onResume")
        val state = viewModel.uiState.value

        if (
            state.orderFlowState == OrderFlowState.ORDER_CREATED_VNPAY_REDIRECTED &&
            !deeplinkHandled &&
            !fallbackHandled
        ) {
            fallbackHandled = true
            viewModel.handleEmptyTransactionId()
            Log.d("RetryPaymentFragment", "transactionId is null")
        } else {
            Log.d("RetryPaymentFragment", "transactionId is not null")
        }
    }

    private fun setUpUi(order: Order) = with(binding) {
        with(order) {
            recipientName.text = fullName
            recipientPhone.text = phone
            recipientAddress.text = shippingAddress
            subtotalValue.text = CurrencyUtils.formatVND(totalAmount)
//            shippingValue.text = CurrencyUtils.formatVND()
            totalValue.text = CurrencyUtils.formatVND(totalAmount)
            adapter.submitList(orderItems)
        }
    }

    fun openUrlInChrome(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.android.chrome")
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(fallbackIntent)
        }
    }
    private fun openPaymentInCustomTab(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        builder.setToolbarColor(requireContext().getColor(R.color.primary))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }
}