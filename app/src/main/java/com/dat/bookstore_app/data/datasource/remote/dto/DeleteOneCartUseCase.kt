package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.repository.CartRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class DeleteOneCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Any?> {
        if (bookId.toString().isBlank() && bookId < 1 ) {
            return Result.Error(message = "Book id is invalid")
        }
        return cartRepository.deleteFromCart(bookId)

    }

}