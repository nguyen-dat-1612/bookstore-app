package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateOrderRequestDTO(
    @Json(name = "fullName")
    val fullName: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "shippingAddress")
    val shippingAddress: String,
    @Json(name = "paymentMethod")
    val paymentMethod: PaymentMethod,
    @Json(name = "items")
    val items: List<OrderItemRequestDTO>,
    @Json(name = "userId")
    val userId: Long
)
