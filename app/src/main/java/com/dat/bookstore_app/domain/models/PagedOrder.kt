package com.dat.bookstore_app.domain.models

data class PagedOrder(
    val orders: List<Order>,
    val current: Int,
    val pageSize: Int,
    val pages: Int,
    val total: Int
) {
    companion object {
        val EMPTY = PagedOrder(emptyList(), 1, 0, 0, 0)
    }
}
