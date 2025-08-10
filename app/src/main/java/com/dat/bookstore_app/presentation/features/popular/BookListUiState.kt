package com.dat.bookstore_app.presentation.features.popular

data class BookListUiState(
    val isLoadData: Boolean = false,
    val totalItems : Int = 0
)
