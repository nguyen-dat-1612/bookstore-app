package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.CartRepository
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
){
    suspend operator fun invoke() = cartRepository.getCart()
}