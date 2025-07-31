package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.CreateOrderRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.OrderItemRequestDTO
import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.repository.OrderRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        fullName: String,
        phone: String,
        shippingAddress: String,
        paymentMethod: PaymentMethod,
        orderItems: List<OrderItemRequestDTO>,
        userId: Long
    ) : Result<Order> {
        if (fullName.isBlank() || phone.isBlank() || shippingAddress.isBlank() || orderItems.isEmpty()) {
            return Result.Error(message = "Invalid input", throwable = Exception("Invalid input"))
        }
        if (orderItems.any { it.quantity <= 0 }) {
            return Result.Error(message = "Invalid input", throwable = Exception("Invalid input"))
        }
        return orderRepository.createOrder(
            CreateOrderRequestDTO(
                fullName = fullName,
                phone = phone,
                shippingAddress = shippingAddress,
                paymentMethod = paymentMethod,
                items = orderItems,
                userId = userId
            )
        )
    }

}