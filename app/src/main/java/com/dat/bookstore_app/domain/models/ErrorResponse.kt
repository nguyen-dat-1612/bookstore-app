package com.dat.bookstore_app.domain.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val statusCode: Int,
    val error: String?,
    val message: String?,
    val data: Any?
)