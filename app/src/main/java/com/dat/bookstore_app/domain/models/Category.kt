package com.dat.bookstore_app.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Category(
    val id: Long,
    val name: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String
) : Parcelable
