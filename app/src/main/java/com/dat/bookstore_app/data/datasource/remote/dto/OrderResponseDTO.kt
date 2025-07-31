package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderResponseDTO(
    @Json(name = "id")
    val id: Long,

    @Json(name = "fullName")
    val fullName: String,

    @Json(name = "phone")
    val phone: String,

    @Json(name = "totalAmount")
    val totalAmount: Double,

    @Json(name = "status")
    val status: OrderStatus,

    @Json(name = "shippingAddress")
    val shippingAddress: String,

    @Json(name = "paymentMethod")
    val paymentMethod: PaymentMethod,

    @Json(name = "userId")
    val userId: Long,

    @Json(name = "orderItems")
    val orderItems: List<OrderItemResponseDTO>,

    @Json(name = "createdAt")
    val createdAt: String ?= null,

    @Json(name = "updatedAt")
    val updatedAt: String ?= null
)