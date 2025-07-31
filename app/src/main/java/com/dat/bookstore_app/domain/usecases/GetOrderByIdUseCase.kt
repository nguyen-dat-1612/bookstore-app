package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.repository.OrderRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class GetOrderByIdUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(id: Long): Result<Order> {
        return orderRepository.getOrderById(id)
    }
}