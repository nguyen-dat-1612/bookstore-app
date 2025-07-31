package com.dat.bookstore_app.presentation.features.search

import com.dat.bookstore_app.domain.models.Book

data class SearchInputUiState (
    val listHistorySearch: List<String> = emptyList()
)