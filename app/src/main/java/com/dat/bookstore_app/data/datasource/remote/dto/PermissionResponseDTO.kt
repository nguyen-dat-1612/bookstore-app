package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PermissionResponseDTO(
    val name: String,
    val path: String,
    val method: String,
    val module: String
    // Không cần roles vì client không xài tới
)