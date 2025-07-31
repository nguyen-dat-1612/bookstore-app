package com.dat.bookstore_app.domain.models

data class OrderItem(
    val id: Long,
    val quantity: Int,
    val price: Double,
    val book: Book
)