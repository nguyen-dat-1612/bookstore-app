package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.PaymentMethod

data class PaymentRequestDTO(
    val orderId: Long,
    val amount: Long,
    val paymentMethod: PaymentMethod,
)
