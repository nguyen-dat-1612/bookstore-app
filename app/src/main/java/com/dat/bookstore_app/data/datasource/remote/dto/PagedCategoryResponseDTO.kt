package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PagedCategoryResponseDTO(
    @Json(name = "meta")
    val paginationDTO: PaginationDTO,
    @Json(name = "result")
    val categories: List<CategoryResponseDTO>
)
