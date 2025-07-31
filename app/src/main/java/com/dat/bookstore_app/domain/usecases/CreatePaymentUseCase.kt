package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.PaymentRequestDTO
import com.dat.bookstore_app.domain.enums.PaymentMethod
import com.dat.bookstore_app.domain.models.Payment
import com.dat.bookstore_app.domain.repository.PaymentRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class CreatePaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(
        orderId : Long,
        amount: Long,
        paymentMethod: PaymentMethod
    ): Result<Payment> {
        if (amount <= 0) {
            return Result.Error(
                message = "Amount must be greater than zero",
                throwable = Exception("Amount must be greater than zero")
            )
        }
        if (paymentMethod == PaymentMethod.COD) {
            return Result.Error(
                message = "Payment method cannot be COD",
                throwable = Exception("Payment method cannot be COD")
            )
        }
        if (orderId <= 0) {
            return Result.Error(
                message = "Invalid order ID",
                throwable = Exception("Invalid order ID")
            )
        }
        val paymentRequestDTO = PaymentRequestDTO(
            orderId = orderId,
            amount = amount,
            paymentMethod = paymentMethod
        )
        return paymentRepository.createPayment(paymentRequestDTO)
    }

}