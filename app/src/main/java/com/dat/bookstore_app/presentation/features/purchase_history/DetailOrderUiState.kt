package com.dat.bookstore_app.presentation.features.purchase_history

import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.models.Payment

data class DetailOrderUiState (
    val order: Order ?= null,
    val payment: Payment ? = null,
    val canCancelOrder: Boolean = false
)