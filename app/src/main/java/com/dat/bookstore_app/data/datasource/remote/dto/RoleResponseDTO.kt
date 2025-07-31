package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class RoleResponseDTO(
    val name: String,
    val description: String?,
    val permissions: List<PermissionResponseDTO>?
)