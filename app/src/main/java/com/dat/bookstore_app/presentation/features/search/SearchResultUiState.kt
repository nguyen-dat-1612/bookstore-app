package com.dat.bookstore_app.presentation.features.search

import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.CategoryUiModel

data class SearchResultUiState (
    val allCategories: List<CategoryUiModel> = emptyList(),
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val sortBy: Sort = Sort.NEW_DESC,
    val query: String? = null,
    val total: Int = 0
)