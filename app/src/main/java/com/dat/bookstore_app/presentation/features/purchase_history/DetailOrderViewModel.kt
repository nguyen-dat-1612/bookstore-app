package com.dat.bookstore_app.presentation.features.purchase_history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.CancelOrderUseCase
import com.dat.bookstore_app.domain.usecases.CreatePaymentUseCase
import com.dat.bookstore_app.domain.usecases.GetOrderByIdUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailOrderViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<DetailOrderUiState>(){

    override fun initState() = DetailOrderUiState()

    val orderId = savedStateHandle.get<Long>("orderId")

    init {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
                val result = getOrderByIdUseCase(orderId!!)
                when(result) {
                    is Result.Success -> {
                        updateState {
                            copy(order = result.data)
                        }
                    }
                    is Result.Error -> {
                        dispatchStateError(e = result.throwable!!)
                    }
            }
            dispatchStateLoading(false)
        }
    }

    fun cancelOrder() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = cancelOrderUseCase(orderId!!)
            when(result) {
                is Result.Success -> {
                    updateState {
                        copy(canCancelOrder = true)
                    }
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                }
            }
        }
    }

    fun createPayment() {
        viewModelScope.launch(exceptionHandler) {
            val order = uiState.value.order
            if (order == null) {
                dispatchStateError(IllegalStateException("Order is null"))
                return@launch
            }

            dispatchStateLoading(true)
            try {
                val result = createPaymentUseCase(
                    orderId = order.id,
                    amount = order.totalAmount.toLong(),
                    paymentMethod = order.paymentMethod
                )

                when (result) {
                    is Result.Success -> {
                        updateState { copy(payment = result.data) }
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