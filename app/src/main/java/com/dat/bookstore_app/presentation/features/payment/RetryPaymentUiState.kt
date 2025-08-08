package com.dat.bookstore_app.presentation.features.payment

import com.dat.bookstore_app.domain.enums.OrderFlowState
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.models.Payment
import com.dat.bookstore_app.domain.models.PaymentResult

data class RetryPaymentUiState(
    val isLoadData: Boolean = false,
    val order: Order? = null,
    val paymentResult: PaymentResult? = null,
    val payment: Payment? = null,
    val orderFlowState: OrderFlowState = OrderFlowState.ORDER_CREATED_COD,
    val countPayment : Int = 0
)
