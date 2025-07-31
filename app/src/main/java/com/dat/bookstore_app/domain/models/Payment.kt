package com.dat.bookstore_app.domain.models

import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.domain.enums.TransactionStatus
import com.squareup.moshi.Json

data class Payment(
    val paymentUrl: String,
    val transactionId: String,
    val paymentMethod: PaymentMethod,
    val status: TransactionStatus,
    val message: String
)
