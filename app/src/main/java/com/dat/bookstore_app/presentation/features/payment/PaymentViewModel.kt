package com.dat.bookstore_app.presentation.features.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.data.datasource.remote.dto.CreateOrderRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.DeleteOneCartUseCase
import com.dat.bookstore_app.data.datasource.remote.dto.OrderItemRequestDTO
import com.dat.bookstore_app.data.mapper.toOrderItemRequestDTO
import com.dat.bookstore_app.domain.enums.OrderFlowState
import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.usecases.CreateOrderUseCase
import com.dat.bookstore_app.domain.usecases.CreatePaymentUseCase
import com.dat.bookstore_app.domain.usecases.GetAccountUseCase
import com.dat.bookstore_app.domain.usecases.GetAddressUseCase
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
    private val getAddressUseCase: GetAddressUseCase,
    private val deleteOneCartUseCase: DeleteOneCartUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<PaymentUiState>(){

    override fun initState() =  PaymentUiState()

    fun loadData(cartList: List<Cart>) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                supervisorScope {
                    val userInfoDeferred = async { getAccountUseCase() }
                    val addressDeferred = async { getAddressUseCase() }

                    val userInfo = userInfoDeferred.await()
                    val address = addressDeferred.await()

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
                    when (address) {
                        is Result.Success -> {
                            updateState {
                                copy(
                                    listAddress = address.data,
                                    chooseAddress = address.data.find { it.isDefault }
                                )
                            }
                        }
                        is Result.Error -> {
                            dispatchStateError(e = address.throwable!!)
                        }
                    }
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

    fun reloadAddresses() {
        viewModelScope.launch(exceptionHandler) {
            when (val address = getAddressUseCase()) {
                is Result.Success -> {
                    updateState {
                        copy(
                            listAddress = address.data,
                            chooseAddress = address.data.find { it.isDefault }
                        )
                    }
                }
                is Result.Error -> {
                    dispatchStateError(address.throwable!!)
                }
            }
        }
    }

    fun createOrderAndMaybePay() {
        viewModelScope.launch(exceptionHandler) {

            dispatchStateLoading(true)
            try {
                val state = uiState.value
                val fullAddress =  uiState.value.chooseAddress?.let {
                    listOfNotNull(
                        it.addressDetail,
                        it.ward,
                        it.province
                    ).joinToString(", ")
                }

                val orderResult = createOrderUseCase(
                    fullName = state.chooseAddress?.fullName!!,
                    phone = state.chooseAddress.phoneNumber!!,
                    shippingAddress = fullAddress!!,
                    paymentMethod = state.paymentMethod,
                    orderItems = state.cartList.toOrderItemRequestDTO(),
                    userId = state.userId
                )

                if (orderResult is Result.Success) {
                    val order = orderResult.data
                    updateState { copy(order = order) }

                    state.cartList.forEach { cartItem ->
                        deleteOneCartUseCase(cartItem.book.id)
                    }

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
                            dispatchStateLoading(false)
                        }
                    } else {
                        updateState {
                            copy(orderFlowState = OrderFlowState.ORDER_CREATED_COD)
                        }
                    }
                } else if (orderResult is Result.Error) {
                    dispatchStateError(orderResult.throwable!!)
                    dispatchStateLoading(false)
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

    fun clearPayment() {
        updateState { copy(payment = null) }
    }

    fun updateChooseAddress(chooseAddress: Address) {
        updateState {
            copy(chooseAddress = listAddress.find { it.id == chooseAddress.id })
        }
    }
}
