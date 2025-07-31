package com.dat.bookstore_app.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val id: Long,
    val quantity: Int,
    val createdAt: String,
    val updatedAt: String,
    val book: Book,
    val isSelected: Boolean = false
): Parcelable
