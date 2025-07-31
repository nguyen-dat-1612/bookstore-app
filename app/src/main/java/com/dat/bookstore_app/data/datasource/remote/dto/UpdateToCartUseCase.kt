package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.domain.repository.CartRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
class UpdateToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(bookId: Long, quantity: Int) : Result<Cart> {
        if (quantity < 1) {
            return Result.Error(message = "Quantity must be greater than 0")
        }
        if (bookId.toString().isBlank() && bookId < 1 ) {
            return Result.Error(message = "Book id is invalid")
        }
        return cartRepository.updateCart(bookId, quantity)
    }
}