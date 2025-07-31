package com.dat.bookstore_app.presentation.features.payment

import com.dat.bookstore_app.domain.models.Order

data class OrderSuccessUiState(
    val order: Order? = null
)
