package com.dat.bookstore_app.domain.models

data class Favorite(
    val id: Long,
    val userId: Long,
    val book: Book
)