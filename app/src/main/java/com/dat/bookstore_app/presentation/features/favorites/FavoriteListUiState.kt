package com.dat.bookstore_app.presentation.features.favorites

import com.dat.bookstore_app.domain.models.PagedFavorite

data class FavoriteListUiState(
    val pagedFavorite: PagedFavorite = PagedFavorite.EMPTY,
    val total: Int = 0,
    val addCartSuccess: Boolean = false
)