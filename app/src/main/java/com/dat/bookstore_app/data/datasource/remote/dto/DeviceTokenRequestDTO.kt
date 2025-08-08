package com.dat.bookstore_app.data.datasource.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceTokenRequestDTO(
    @Json(name = "userId")
    val userId: Long,
    @Json(name = "deviceToken")
    val deviceToken: String = "",
    @Json(name = "deviceType")
    val deviceType: String = "ANDROID"
)
