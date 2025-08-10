package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatePasswordRequestDTO(
    @Json(name = "password")
    val password: String,
    @Json(name = "confirmPassword")
    val confirmPassword: String
)
