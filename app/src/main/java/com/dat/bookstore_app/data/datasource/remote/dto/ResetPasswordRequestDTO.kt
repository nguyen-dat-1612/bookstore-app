package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResetPasswordRequestDTO(
    @Json(name = "token")
    val token: String,
    @Json(name = "newPassword")
    val newPassword: String,
    @Json(name = "confirmPassword")
    val confirmPassword: String
)
