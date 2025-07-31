package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.network.Result


interface CartRepository {
    suspend fun getCart(): Result<List<Cart>>

    suspend fun addToCart(bookId: Long, quantity: Int): Result<Cart>

    suspend fun deleteFromCart(bookId: Long): Result<Any?>

    suspend fun updateCart(bookId: Long, quantity: Int): Result<Cart>

    suspend fun clearCart(): Result<Any?>
}