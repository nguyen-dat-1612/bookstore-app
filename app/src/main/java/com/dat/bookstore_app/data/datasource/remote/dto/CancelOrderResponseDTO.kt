package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.OrderStatus
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CancelOrderResponseDTO(
    @Json(name = "orderId")
    val orderId: Long,
    @Json(name = "status")
    val status: OrderStatus
)
