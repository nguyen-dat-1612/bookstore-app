package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PagedBookResponseDTO (
    @Json(name = "meta")
    val paginationDTO: PaginationDTO,
    @Json(name = "result")
    val books: List<BookResponseDTO>
)