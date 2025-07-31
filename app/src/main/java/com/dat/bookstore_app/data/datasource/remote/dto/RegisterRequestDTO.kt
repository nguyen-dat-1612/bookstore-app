package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequestDTO (
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String
)