package com.dat.bookstore_app.presentation.features.home

import com.dat.bookstore_app.domain.models.Banner
import com.dat.bookstore_app.domain.models.HomeBooksResult
import com.dat.bookstore_app.domain.models.PagedBook

data class HomeUiState (
    val homeBooksResult: HomeBooksResult = HomeBooksResult(
        popularBooks = PagedBook.EMPTY,
        newBooks = PagedBook.EMPTY,
        lowToHighPriceBooks = PagedBook.EMPTY,
        highToLowPriceBooks = PagedBook.EMPTY
    ),
    val homeBanners: List<Banner> = emptyList()
)