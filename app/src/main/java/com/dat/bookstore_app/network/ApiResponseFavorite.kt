package com.dat.bookstore_app.network

import com.dat.bookstore_app.data.datasource.remote.dto.FavoriteResponseDTO
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteApiResponse(
    @Json(name = "statusCode")
    val statusCode : Int,
    @Json(name = "message")
    val message : String,
    @Json(name = "data")
    val data : FavoriteResponseDTO?,
    @Json(name = "error")
    val error: String? = null
)