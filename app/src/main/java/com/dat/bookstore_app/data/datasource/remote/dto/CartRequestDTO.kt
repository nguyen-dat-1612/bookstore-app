package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CartRequestDTO(
    @Json(name = "bookId")
    val bookId: Long,
    @Json(name = "quantity")
    val quantity: Int

)
