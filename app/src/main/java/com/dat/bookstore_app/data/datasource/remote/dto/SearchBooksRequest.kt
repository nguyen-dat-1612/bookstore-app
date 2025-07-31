package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.Sort

data class SearchBooksRequest(
    val title: String? = null,
    val selectedCategoryIds: List<Long> = emptyList(),
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val sortBy: Sort,
    val page: Int,
    val pageSize: Int,
)