package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.TransactionStatus

data class PaymentResultDTO(
    val transactionId: String,
    val status: TransactionStatus,
    val message: String
)
