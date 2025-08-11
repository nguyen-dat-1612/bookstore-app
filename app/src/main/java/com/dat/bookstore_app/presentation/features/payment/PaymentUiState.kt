package com.dat.bookstore_app.presentation.features.payment

import com.dat.bookstore_app.domain.enums.OrderFlowState
import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.models.Payment
import com.dat.bookstore_app.domain.models.PaymentResult

data class PaymentUiState(
    // Trạng thái load dữ liệu
    val isLoadData: Boolean = false,
    val listAddress: List<Address> = emptyList(),
    val chooseAddress: Address? = null,

    val paymentMethod: PaymentMethod = PaymentMethod.COD,
    val cartList: List<Cart> = emptyList(),
    val subtotal: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val userId: Long = 0,

    val order: Order ?= null,
    val payment: Payment ?= null,
    val paymentResult: PaymentResult?= null,

    val orderFlowState: OrderFlowState? = null // <- QUAN TRỌNG NHẤT
)
