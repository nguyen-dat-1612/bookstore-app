package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryResponseDTO(
    @Json(name = "id")
    val id: Long? = null,
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "createdAt")
    val createdAt: String? = null,
    @Json(name = "updatedAt")
    val updatedAt: String? = null
)