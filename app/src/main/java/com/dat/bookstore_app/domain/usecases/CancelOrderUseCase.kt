package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.OrderRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class CancelOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: Long) : Result<Any?> {
        return orderRepository.cancelOrder(orderId)
    }
}