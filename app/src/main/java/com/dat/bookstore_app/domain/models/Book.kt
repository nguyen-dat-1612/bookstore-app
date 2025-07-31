package com.dat.bookstore_app.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: Long,
    val thumbnail: String,
    val slider: List<String>,
    val title: String,
    val author: String,
    val price: Double,
    val quantity: Int,
    val category: Category,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val discount: Int,
    val sold: Int,
    val age: Int,
    val publicationDate: String,
    val publisher: String,
    val language: String,
    val pageCount: Int,
    val coverType: String
) : Parcelable
