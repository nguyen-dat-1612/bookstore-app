package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class UserLogin(
    @Json(name = "email")
    val email: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "fullName")
    val fullName: String,
    @Json(name = "address")
    val address: String,
    @Json(name = "role")
    val role: String,
    @Json(name = "id")
    val id: Long,
    @Json(name = "avatar")
    val avatar: String?,
    @Json(name = "permissions")
    val permissions: List<PermissionResponseDTO>
)