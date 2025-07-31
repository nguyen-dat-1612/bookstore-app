package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaginationDTO (
    @Json(name = "current")
    val current: Int,
    @Json(name = "pageSize")
    val pageSize: Int,
    @Json(name = "pages")
    val pages: Int,
    @Json(name = "total")
    val total: Int
)