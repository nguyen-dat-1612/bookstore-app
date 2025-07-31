package com.dat.bookstore_app.presentation.features.book

import com.dat.bookstore_app.domain.models.Book

data class DetailUiState(
    val book: Book? = null,
    val count: Int = 1,
    val addCartSuccess: Boolean = false,
)
