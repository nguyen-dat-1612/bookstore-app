package com.dat.bookstore_app.domain.models

data class PagedBook(
    val books: List<Book>,
    val current: Int,
    val pageSize: Int,
    val pages: Int,
    val total: Int
) {
    companion object {
        val EMPTY = PagedBook(emptyList(), 1, 0, 0, 0)
    }
}