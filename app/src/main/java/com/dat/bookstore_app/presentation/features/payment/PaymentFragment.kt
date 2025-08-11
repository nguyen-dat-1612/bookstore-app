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
import com.dat.bookstore_app.presentation.common.adapter.AddressAdapter
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
    private val addressAdapter by lazy {
        AddressAdapter(
            onAddressSelected = {
                viewModel.updateChooseAddress(it)
            }
        )
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
        rvAddresss.adapter = addressAdapter

        btnBack.setOnClickListener {
            navController.popBackStack()
        }

        btnOrder.setOnClickListener {
//            requireActivity().findViewById<View>(R.id.progressOverlay).visibility = View.VISIBLE
            if (viewModel.uiState.value.chooseAddress == null) {
                showToast("Bạn chưa có hoặc chưa chọn địa chỉ giao hàng")
                return@setOnClickListener
            }
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
        btnAddAddress.setOnClickListener{
            navController.navigate(PaymentFragmentDirections.actionPaymentFragmentToAddAddressFragment())
        }
        binding.btnAddAddressEmpty.setOnClickListener {
            navController.navigate(PaymentFragmentDirections.actionPaymentFragmentToAddAddressFragment())
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
                                requireActivity().findViewById<View>(R.id.progressOverlay).visibility = View.GONE
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
                launch {
                    viewModel.errorsState.errors.collect { throwable ->
                        Toast.makeText(requireContext(), throwable.message ?: "Lỗi không xác định", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
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

        if (viewModel.uiState.value.order == null) {
            viewModel.reloadAddresses()
        }
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
        subtotalValue.text = CurrencyUtils.formatVND(uiState.subtotal)
        shippingValue.text = CurrencyUtils.formatVND(uiState.shipping)
        totalValue.text = CurrencyUtils.formatVND(uiState.total)
        adapter.submitList(uiState.cartList)

        if (uiState.listAddress.isEmpty()) {
            rvAddresss.visibility = View.GONE
            emptyAddressContainer.visibility = View.VISIBLE
            btnAddAddress.visibility = View.GONE
        } else {
            rvAddresss.visibility = View.VISIBLE
            emptyAddressContainer.visibility = View.GONE
            btnAddAddress.visibility = View.VISIBLE
            addressAdapter.submitList(uiState.listAddress)
        }
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