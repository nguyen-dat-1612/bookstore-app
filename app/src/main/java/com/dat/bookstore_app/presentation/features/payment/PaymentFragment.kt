package com.dat.bookstore_app.presentation.features.payment

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
import androidx.navigation.fragment.navArgs
import com.dat.bookstore_app.databinding.FragmentPaymentBinding
import com.dat.bookstore_app.presentation.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import com.dat.bookstore_app.R
import com.dat.bookstore_app.domain.enums.OrderFlowState
import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.presentation.common.adapter.BookPaymentAdapter
import com.dat.bookstore_app.utils.helpers.CurrencyUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentFragment : BaseFragment<FragmentPaymentBinding>() {

    private val viewModel: PaymentViewModel by viewModels()
    private val navController by lazy {
        requireActivity().findNavController(R.id.nav_host_main)
    }
    private val args by navArgs<PaymentFragmentArgs>()
    private val adapter by lazy {
        BookPaymentAdapter()
    }
    private var deeplinkHandled = false
    private var fallbackHandled = false

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentBinding {
        return FragmentPaymentBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        if (!viewModel.uiState.value.isLoadData) {
            viewModel.loadData(args.cartList.toList())
        }
        rvBookPayment.adapter = adapter

        btnBack.setOnClickListener {
            navController.popBackStack()
        }

        btnOrder.setOnClickListener {
            requireActivity().findViewById<View>(R.id.progressOverlay).visibility = View.VISIBLE
            viewModel.createOrderAndMaybePay()
        }

        momoPaymentOption.setOnClickListener {
            momoRadioButton.isChecked = true
            vnpayRadioButton.isChecked = false
            viewModel.updatePaymentMethod(PaymentMethod.COD)
        }

        vnpayPaymentOption.setOnClickListener {
            vnpayRadioButton.isChecked = true
            momoRadioButton.isChecked = false
            viewModel.updatePaymentMethod(PaymentMethod.VNPAY)
        }

        btnChangeAddress.setOnClickListener {
            with(viewModel.uiState.value) {
                navController.navigate(
                    PaymentFragmentDirections.actionPaymentFragmentToChangeAddressFragment(
                        fullName = fullName,
                        address = shippingAddress,
                        phone = phone
                    )
                )
            }
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loadingState.loading.collect {
                        requireActivity().findViewById<View>(R.id.progressOverlay).visibility = if (it) View.VISIBLE else View.GONE
//                        binding.layoutViewProgress.root.visibility = View.VISIBLE
                    }
                }
                launch {
                    viewModel.uiState.collectLatest { uiState ->

                        setUpUi(uiState)

                        uiState.payment?.let { payment ->
                            viewModel.clearPayment()
                            openPaymentInCustomTab(payment.paymentUrl)
//                            openUrlInChrome(requireActivity(), payment.paymentUrl)
                        }

                        val orderId = uiState.order?.id
                        val flowState = uiState.orderFlowState


                        val shouldNavigate = when (flowState) {
                            OrderFlowState.ORDER_CREATED_COD -> true

                            OrderFlowState.ORDER_VNPAY_SUCCESS -> {
                                Toast.makeText(requireContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show()
                                true
                            }

                            OrderFlowState.ORDER_VNPAY_FAILED,
                            OrderFlowState.ORDER_VNPAY_PENDING,
                            OrderFlowState.ORDER_VNPAY_EMPTY_RESULT -> {
                                Toast.makeText(requireContext(), "Đơn hàng đang chờ thanh toán. Vui lòng thanh toán trong vòng 24h.", Toast.LENGTH_LONG).show()
                                true
                            }

                            else -> false
                        }

                        if (shouldNavigate && orderId != null) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate(
                                    PaymentFragmentDirections.actionPaymentFragmentToOrderSuccessFragment(orderId)
                                )
                            }, 2000) // 🕒 Delay 2s
                        }


                        when (uiState.paymentMethod) {
                            PaymentMethod.COD -> binding.momoRadioButton.isChecked = true
                            PaymentMethod.VNPAY -> binding.vnpayRadioButton.isChecked = true
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<Triple<String, String, String>>("new_address")
            ?.observe(viewLifecycleOwner) { (fullName, phone, address) ->
                viewModel.updateAddress(fullName, phone, address)

                navController.currentBackStackEntry?.savedStateHandle
                    ?.remove<Triple<String, String, String>>("new_address")
            }

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
            }
    }

    override fun onResume() {
        super.onResume()

        val state = viewModel.uiState.value
        if (
            state.orderFlowState == OrderFlowState.ORDER_CREATED_VNPAY_REDIRECTED &&
            !deeplinkHandled &&
            !fallbackHandled
        ) {
            fallbackHandled = true
            viewModel.handleEmptyTransactionId()
        }
    }

    private fun setUpUi(uiState: PaymentUiState) = with(binding) {
        recipientName.text = uiState.fullName
        recipientPhone.text = uiState.phone
        recipientAddress.text = uiState.shippingAddress
        subtotalValue.text = CurrencyUtils.formatVND(uiState.subtotal)
        shippingValue.text = CurrencyUtils.formatVND(uiState.shipping)
        totalValue.text = CurrencyUtils.formatVND(uiState.total)
        adapter.submitList(uiState.cartList)
    }

    private fun openPaymentInCustomTab(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(true)
        builder.setToolbarColor(requireContext().getColor(R.color.primary))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
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
}