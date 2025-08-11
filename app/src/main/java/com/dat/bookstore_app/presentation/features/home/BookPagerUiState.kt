package com.dat.bookstore_app.presentation.features.home

import com.dat.bookstore_app.domain.models.Book

data class BookPagerUiState(
    val isLoadData : Boolean = false,
    val bookList : List<Book> = emptyList(),
)
