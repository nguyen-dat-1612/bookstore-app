package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json

data class OrderItemRequestDTO (
    @Json(name = "bookId")
    val bookId: Long,
    @Json(name = "quantity")
    val quantity: Int
)