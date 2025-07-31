package com.dat.bookstore_app.presentation.features.cart

import com.dat.bookstore_app.domain.models.Cart

data class CartUiState(
    val ListCart: List<Cart> = emptyList(),
)
