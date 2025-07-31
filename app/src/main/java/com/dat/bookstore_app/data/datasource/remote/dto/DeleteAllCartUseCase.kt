package com.dat.bookstore_app.data.datasource.remote.dto

import com.dat.bookstore_app.domain.repository.CartRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class DeleteAllCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Result<Any?> {
        return cartRepository.clearCart()
    }

}