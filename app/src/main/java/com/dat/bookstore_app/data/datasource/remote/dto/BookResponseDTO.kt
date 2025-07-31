package com.dat.bookstore_app.data.datasource.remote.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookResponseDTO(
    @Json(name = "id")
    val id: Long? = null,
    @Json(name = "thumbnail")
    val thumbnail: String? = null,
    @Json(name = "slider")
    val slider: List<String>? = null,
    @Json(name = "title")
    val title: String? = null,
    @Json(name = "author")
    val author: String? = null,
    @Json(name = "price")
    val price: Double? = null,
    @Json(name = "quantity")
    val quantity: Int? = null,
    @Json(name = "category")
    val category: CategoryResponseDTO? = null,
    @Json(name = "description")
    val description: String? = null,
    @Json(name = "createdAt")
    val createdAt: String? = null,
    @Json(name = "updatedAt")
    val updatedAt: String? = null,
    @Json(name = "discount")
    val discount: Int? = null,
    @Json(name = "sold")
    val sold: Int? = null,
    @Json(name = "age")
    val age: Int? = null,
    @Json(name = "publicationDate")
    val publicationDate: String? = null,
    @Json(name = "publisher")
    val publisher: String? = null,
    @Json(name = "language")
    val language: String? = null,
    @Json(name = "pageCount")
    val pageCount: Int? = null,
    @Json(name = "coverType")
    val coverType: String? = null
)
