package com.dat.bookstore_app.domain.models

data class HomeBooksResult(
    val popularBooks: PagedBook,
    val newBooks: PagedBook,
    val lowToHighPriceBooks: PagedBook,
    val highToLowPriceBooks: PagedBook
)