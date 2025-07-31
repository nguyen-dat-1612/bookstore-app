package com.dat.bookstore_app.presentation.features.purchase_history

import androidx.paging.PagingData
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.Order

data class OrderListState (
    val isLoading: Boolean = false,
    val total: Int = 0,
    val currentFilter: OrderStatus = OrderStatus.ALL,
    val currentSort: Sort = Sort.NEW_DESC

)