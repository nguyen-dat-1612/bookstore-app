package com.dat.bookstore_app.presentation.features.payment


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentBinding {
        return FragmentPaymentBinding.inflate(inflater, container, false)
    }

    override fun setUpView() = with(binding) {
        viewModel.loadData(args.cartList.toList())
        rvBookPayment.adapter = adapter

        btnBack.setOnClickListener {
            navController.popBackStack()
        }

        btnOrder.setOnClickListener {
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
                viewModel.uiState.collectLatest { uiState ->

                    setUpUi(uiState)

                    uiState.payment?.let { payment ->
                        viewModel.clearPayment() // để không bị trigger lại

                        // Mở VNPay qua Chrome Custom Tab
//                        openPaymentInCustomTab(payment.paymentUrl)
                        openUrlInChrome(requireActivity(), payment.paymentUrl)

                    }

                    if (uiState.paymentSuccess) {
                        Toast.makeText(requireContext(), "Thanh toán thành công", Toast.LENGTH_SHORT).show()
                        uiState.order?.id?.let {
                            navController.navigate(
                                PaymentFragmentDirections
                                    .actionPaymentFragmentToOrderSuccessFragment(
                                        orderId = it
                                    )
                            )
                        }
                    }
                    when (uiState.paymentMethod) {
                        PaymentMethod.COD -> binding.momoRadioButton.isChecked = true
                        PaymentMethod.VNPAY -> binding.vnpayRadioButton.isChecked = true
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
    }

    private fun setUpUi(uiState: PaymentUiState) = with(binding){
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

        // Nếu Chrome không có thì mở bằng trình duyệt mặc định
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(fallbackIntent)
        }
    }

    override fun onResume() {
        super.onResume()

        val transactionId = viewModel.uiState.value.payment?.transactionId
        val paymentMethod = viewModel.uiState.value.payment?.paymentMethod

        if (transactionId != null && paymentMethod == PaymentMethod.VNPAY) {
            viewModel.checkTransactionStatus(transactionId)
        }
    }
}