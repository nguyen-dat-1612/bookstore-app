package com.dat.bookstore_app.domain.models

import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.enums.PaymentMethod

data class Order(
    val id: Long,
    val fullName: String,
    val phone: String,
    val totalAmount: Double,
    val status: OrderStatus,
    val shippingAddress: String,
    val paymentMethod: PaymentMethod,
    val userId: Long,
    val orderItems: List<OrderItem>,
    val createdAt: String,
    val updatedAt: String
)
