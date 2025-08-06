package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.Sort

data class GetFavoriteRequest (
    val page: Int,
    val size: Int,
    val sort: Sort
)