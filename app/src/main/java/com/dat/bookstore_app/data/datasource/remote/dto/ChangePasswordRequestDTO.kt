package com.dat.bookstore_app.data.datasource.remote.dto

import androidx.annotation.NonNull
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangePasswordRequestDTO(
    @Json(name = "id")
    val id: Long,
    @Json(name = "oldPassword")
    val oldPassword: String,
    @Json(name = "newPassword")
    val newPassword: String
)
