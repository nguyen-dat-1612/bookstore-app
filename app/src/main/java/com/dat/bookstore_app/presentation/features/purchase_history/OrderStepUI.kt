package com.dat.bookstore_app.presentation.features.purchase_history

import com.dat.bookstore_app.domain.enums.OrderStatus

data class OrderStepUI(
    val status: OrderStatus,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val isCanceled: Boolean,
    val createdAt: String? = null,
    val updatedAt: String? = null
)