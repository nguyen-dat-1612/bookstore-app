package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class FileResponseDTO(
    @Json(name = "url")
    val url: String,
    @Json(name = "fileName")
    val fileName: String,
    @Json(name = "uploadAt")
    val uploadAt: Date
)
