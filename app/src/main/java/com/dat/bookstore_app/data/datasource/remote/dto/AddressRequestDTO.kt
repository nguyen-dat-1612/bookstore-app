package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.AddressType
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddressRequestDTO(
    @Json(name = "fullName")
    val fullName: String,
    @Json(name = "phoneNumber")
    val phoneNumber: String,
    @Json(name = "province")
    val province: String,
    @Json(name = "ward")
    val ward: String,
    @Json(name = "addressDetail")
    val addressDetail: String,
    @Json(name = "addressType")
    val addressType: AddressType,
    @Json(name = "is_default")
    val isDefault: Boolean
)