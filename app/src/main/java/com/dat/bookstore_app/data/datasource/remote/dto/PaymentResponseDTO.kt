package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.domain.enums.TransactionStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentResponseDTO(
    @Json(name = "paymentUrl")
    val paymentUrl: String,
    @Json(name = "transactionId")
    val transactionId: String,
    @Json(name = "paymentMethod")
    val paymentMethod: PaymentMethod,
    @Json(name = "status")
    val status: TransactionStatus,
    @Json(name = "message")
    val message: String
)
