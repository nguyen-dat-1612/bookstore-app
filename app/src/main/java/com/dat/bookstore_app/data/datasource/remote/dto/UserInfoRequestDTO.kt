package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfoRequestDTO(
    val id: Long,
    val fullName: String,
    val address: String,
    val phone: String,
    val avatar: String? = null // avatar không bắt buộc, nên để nullable
)