package com.dat.bookstore_app.presentation.features.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.data.datasource.remote.dto.CreateOrderRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.DeleteOneCartUseCase
import com.dat.bookstore_app.data.datasource.remote.dto.OrderItemRequestDTO
import com.dat.bookstore_app.data.mapper.toOrderItemRequestDTO
import com.dat.bookstore_app.domain.enums.OrderFlowState
import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.usecases.CreateOrderUseCase
import com.dat.bookstore_app.domain.usecases.CreatePaymentUseCase
import com.dat.bookstore_app.domain.usecases.GetAccountUseCase
import com.dat.bookstore_app.domain.usecases.GetBookByIdUseCase
import com.dat.bookstore_app.domain.usecases.GetTransactionStatusUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val getTransactionStatusUseCase: GetTransactionStatusUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<PaymentUiState>(){

    override fun initState() =  PaymentUiState()

    fun loadData(cartList: List<Cart>) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                supervisorScope {
                    val userInfoDeferred = async { getAccountUseCase() }
                    val userInfo = userInfoDeferred.await()

                    val subtotal = cartList.sumOf {
                        val price = it.book.price
                        val discount = it.book.discount
                        val finalPrice = price * (100 - discount) / 100.0
                        finalPrice * it.quantity
                    }

                    val shipping = 30000.0
                    val total = subtotal + shipping

                    when (userInfo) {
                        is Result.Success -> {
                            val user = userInfo.data
                            updateState {
                                copy(
                                    isLoadData = true,
                                    fullName = user.fullName ?: "Chưa có tên",
                                    phone = user.phone ?: "Chưa có số",
                                    shippingAddress = user.address ?: "Chưa có địa chỉ",
                                    userId = user.id ?: 0L,
                                    cartList = cartList,
                                    subtotal = subtotal,
                                    shipping = shipping,
                                    total = total
                                )
                            }
                        }

                        is Result.Error -> {
                            dispatchStateError(e = userInfo.throwable!!)
                        }
                    }
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

    fun createOrderAndMaybePay() {
        viewModelScope.launch(exceptionHandler) {

            if (!validateOrderInfo()) {
                dispatchStateLoading(false)
                return@launch
            }

            dispatchStateLoading(true)
            try {
                val state = uiState.value
                val orderResult = createOrderUseCase(
                    fullName = state.fullName,
                    phone = state.phone,
                    shippingAddress = state.shippingAddress,
                    paymentMethod = state.paymentMethod,
                    orderItems = state.cartList.toOrderItemRequestDTO(),
                    userId = state.userId
                )

                if (orderResult is Result.Success) {
                    val order = orderResult.data
                    updateState { copy(order = order) }

                    if (state.paymentMethod == PaymentMethod.VNPAY) {
                        val paymentResult = createPaymentUseCase(
                            orderId = order.id,
                            amount = order.totalAmount.toLong(),
                            paymentMethod = PaymentMethod.VNPAY
                        )

                        if (paymentResult is Result.Success) {
                            updateState {
                                copy(
                                    payment = paymentResult.data,
                                    orderFlowState = OrderFlowState.ORDER_CREATED_VNPAY_REDIRECTED
                                )
                            }
                        } else if (paymentResult is Result.Error) {
                            dispatchStateError(paymentResult.throwable!!)
                        }
                    } else {
                        updateState {
                            copy(orderFlowState = OrderFlowState.ORDER_CREATED_COD)
                        }
                    }
                } else if (orderResult is Result.Error) {
                    dispatchStateError(orderResult.throwable!!)
                }
            } finally {
//                dispatchStateLoading(false)
            }
        }
    }

    fun checkTransactionStatus(transactionId: String) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = getTransactionStatusUseCase(transactionId)
                when (result) {
                    is Result.Success -> {
                        val paymentResult = result.data
                        val newFlowState = when (paymentResult.status.name) {
                            "SUCCESS" -> OrderFlowState.ORDER_VNPAY_SUCCESS
                            "FAILED" -> OrderFlowState.ORDER_VNPAY_FAILED
                            else -> OrderFlowState.ORDER_VNPAY_PENDING
                        }
                        updateState {
                            copy(
                                paymentResult = paymentResult,
                                orderFlowState = newFlowState
                            )
                        }
                    }
                    is Result.Error -> {
                        dispatchStateError(e = result.throwable!!)
                    }
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

    fun handleEmptyTransactionId() {
        updateState {
            copy(orderFlowState = OrderFlowState.ORDER_VNPAY_EMPTY_RESULT)
        }
    }

    fun updatePaymentMethod(method: PaymentMethod) {
        updateState {
            copy(paymentMethod = method)
        }
    }

    fun updateAddress(fullName: String, phone: String, address: String) {
        updateState {
            copy(
                fullName = fullName,
                phone = phone,
                shippingAddress = address
            )
        }
    }

    fun clearPayment() {
        updateState { copy(payment = null) }
    }

    private fun validateOrderInfo(): Boolean {
        val state = uiState.value

        // Chặn placeholder hoặc rỗng
        if (state.fullName.isNullOrBlank() || state.fullName == "Chưa có tên") {
            dispatchStateError(IllegalArgumentException("Vui lòng nhập họ tên"))
            return false
        }
        if (state.phone.isNullOrBlank() || state.phone == "Chưa có số" || !state.phone.matches(Regex("^\\d{9,11}\$"))) {
            dispatchStateError(IllegalArgumentException("Số điện thoại không hợp lệ"))
            return false
        }
        if (state.shippingAddress.isNullOrBlank() || state.shippingAddress == "Chưa có địa chỉ") {
            dispatchStateError(IllegalArgumentException("Vui lòng nhập địa chỉ giao hàng"))
            return false
        }
        if (state.cartList.isEmpty()) {
            dispatchStateError(IllegalArgumentException("Giỏ hàng trống"))
            return false
        }
        return true
    }

}
