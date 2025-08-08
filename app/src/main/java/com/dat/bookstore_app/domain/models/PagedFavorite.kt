package com.dat.bookstore_app.domain.models

class PagedFavorite (
    val favorites: List<Favorite>,
    val current: Int,
    val pageSize: Int,
    val pages: Int,
    val total: Int
    ) {
    companion object {
        val EMPTY = PagedFavorite(emptyList(), 1, 0, 0, 0)
    }
}