package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.enums.Sort

data class SearchOrdersRequest(
    val page: Int,
    val size: Int,
    val sortBy: Sort,
    val filter: OrderStatus? = null
)