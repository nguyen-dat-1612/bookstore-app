package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteResponseDTO (
    @Json(name = "id")
    val id: Long,
    @Json(name = "userId")
    val userId: Long,
    @Json(name = "book")
    val book: BookResponseDTO
)