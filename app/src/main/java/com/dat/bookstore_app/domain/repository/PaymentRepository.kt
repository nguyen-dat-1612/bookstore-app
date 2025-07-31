package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.data.datasource.remote.dto.PaymentRequestDTO
import com.dat.bookstore_app.domain.models.Payment
import com.dat.bookstore_app.domain.models.PaymentResult
import com.dat.bookstore_app.network.Result

interface PaymentRepository {
    suspend fun createPayment(paymentRequestDTO : PaymentRequestDTO): Result<Payment>
    suspend fun getTransactionStatus(transactionId: String): Result<PaymentResult>
}