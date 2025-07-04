package com.dat.bookstore_app.data.datasource.remote.dto

data class LoginResponse (
    val token: String,
    val refreshToken: String
)