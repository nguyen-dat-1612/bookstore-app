package com.dat.bookstore_app.presentation.features.book

import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.models.Favorite

data class DetailUiState(
    val book: Book? = null,
    val count: Int = 1,
    val addCartSuccess: Boolean = false,
    val addFavoriteSuccess: Boolean = false,
    val isFavorite: Boolean = false,
    val favorite: Favorite? = null
)
