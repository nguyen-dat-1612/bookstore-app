package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInsideToken(
    val id: Long,
    val email: String,
    val fullName: String
)