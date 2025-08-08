package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterResponseDTO(
    @Json(name = "id")
    val id: Long,
    @Json(name = "fullName")
    val fullName: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "phone")
    val phone: String
)
