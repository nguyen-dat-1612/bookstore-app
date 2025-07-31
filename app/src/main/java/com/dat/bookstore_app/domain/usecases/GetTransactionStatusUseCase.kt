package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.models.PaymentResult
import com.dat.bookstore_app.domain.repository.PaymentRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class GetTransactionStatusUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(transactionId: String) : Result<PaymentResult> {
        if (transactionId.isEmpty()) {
            return Result.Error(
                message = "Transaction ID cannot be empty",
                throwable = Exception("Transaction ID cannot be empty")
            )
        }
        return paymentRepository.getTransactionStatus(transactionId)
    }
}