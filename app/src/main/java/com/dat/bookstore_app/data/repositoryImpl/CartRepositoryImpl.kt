package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.remote.api.CartApi
import com.dat.bookstore_app.data.datasource.remote.dto.CartRequestDTO
import com.dat.bookstore_app.data.mapper.toCartDomain
import com.dat.bookstore_app.data.mapper.toDomain
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.domain.repository.CartRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.dat.bookstore_app.utils.extension.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi
) : CartRepository{
    override suspend fun getCart(): Result<List<Cart>> {
        return apiCallResponse {
            cartApi.getCart()
        }.map { it.toDomain() }
    }

    override suspend fun addToCart(bookId: Long, quantity: Int): Result<Cart> {
        return apiCallResponse {
            cartApi.addToCart(CartRequestDTO(bookId = bookId, quantity = quantity))
        }.map { it.toCartDomain() }
    }

    override suspend fun deleteFromCart(bookId: Long): Result<Any?> {
        return apiCallResponse {
            cartApi.deleteFromCart(bookId)
        }
    }

    override suspend fun updateCart(bookId: Long, quantity: Int): Result<Cart> {
        return apiCallResponse {
            cartApi.updateCart(CartRequestDTO(bookId = bookId, quantity = quantity))
        }.map { it.toCartDomain() }
    }

    override suspend fun clearCart(): Result<Any?> {
        return apiCallResponse {
            cartApi.clearCart()
        }
    }


}