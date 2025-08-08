package com.dat.bookstore_app.presentation.features.payment

import android.app.AlertDialog
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.enums.OrderFlowState
import com.dat.bookstore_app.domain.usecases.CancelOrderUseCase
import com.dat.bookstore_app.domain.usecases.CreatePaymentUseCase
import com.dat.bookstore_app.domain.usecases.GetOrderByIdUseCase
import com.dat.bookstore_app.domain.usecases.GetTransactionStatusUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

@HiltViewModel
class RetryPaymentViewModel @Inject constructor(
    private val getTransactionStatusUseCase: GetTransactionStatusUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val cancelOrderUseCase: CancelOrderUseCase
) : BaseViewModel<RetryPaymentUiState>() {

    override fun initState() = RetryPaymentUiState()

    val orderId = savedStateHandle.get<Long>("orderId")

    init {
        viewModelScope.launch (exceptionHandler){
            dispatchStateLoading(true)
            try {
                val result = getOrderByIdUseCase(orderId!!)
                when(result) {
                    is Result.Success -> {
                        val order = result.data
                        updateState { copy(order = order) }
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

    fun createPayment() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val state = uiState.value
                val paymentResult = createPaymentUseCase(
                    orderId = state.order!!.id,
                    amount = state.order!!.totalAmount.toLong(),
                    paymentMethod = state.order!!.paymentMethod
                )
                when (paymentResult) {
                    is Result.Success -> {
                        val payment = paymentResult.data
                        updateState {
                            copy(
                                payment = payment,
                                orderFlowState = OrderFlowState.ORDER_CREATED_VNPAY_REDIRECTED
                            )
                        }
                    }

                    is Result.Error -> {
                        dispatchStateError(e = paymentResult.throwable!!)
                    }
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

    fun updatePayment() {
        Log.d("RetryPaymentViewModel", "updatePayment: ${uiState.value.countPayment}")
        if (uiState.value.countPayment >= 3) {
            updateState {
                copy(
                    payment = null,
                    orderFlowState = OrderFlowState.ORDER_CANCELLED
                )
            }

        } else {
            updateState {
                copy(
                    payment = null,
                    countPayment = countPayment + 1
                )
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
            copy(
                orderFlowState = OrderFlowState.ORDER_VNPAY_EMPTY_RESULT
            )
        }
    }

    fun cancelOrder() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = cancelOrderUseCase(orderId!!)
                when (result) {
                    is Result.Success -> {
                        updateState {
                            copy(
                                orderFlowState = OrderFlowState.ORDER_CANCELLED
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

}