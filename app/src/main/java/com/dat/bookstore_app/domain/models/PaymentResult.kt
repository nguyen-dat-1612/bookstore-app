package com.dat.bookstore_app.domain.models

import com.dat.bookstore_app.domain.enums.TransactionStatus

data class PaymentResult (
    val transactionId: String,
    val status: TransactionStatus,
    val message: String
)