package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CartResponseDTO (
    @Json(name = "id")
    val id: Long,
    @Json(name = "quantity")
    val quantity: Int,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "updatedAt")
    val updatedAt: String,
    @Json(name = "book")
    val book: BookResponseDTO
)