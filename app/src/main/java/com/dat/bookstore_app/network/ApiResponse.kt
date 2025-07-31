package com.dat.bookstore_app.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse <T> (
    @Json(name = "statusCode")
    val statusCode : Int,
    @Json(name = "message")
    val message :  String,
    @Json(name = "data")
    val data : T?,
    @Json(name = "error")
    val error: String ?= null
)