package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.remote.api.PaymentApi
import com.dat.bookstore_app.data.datasource.remote.dto.PaymentRequestDTO
import com.dat.bookstore_app.data.mapper.toDomain
import com.dat.bookstore_app.domain.models.Payment
import com.dat.bookstore_app.domain.models.PaymentResult
import com.dat.bookstore_app.domain.repository.PaymentRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.dat.bookstore_app.utils.extension.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val paymentApi: PaymentApi
) : PaymentRepository {

    override suspend fun createPayment(paymentRequestDTO: PaymentRequestDTO): Result<Payment> {
        return apiCallResponse {
            paymentApi.createPayment(paymentRequestDTO)
        }.map {
            it.toDomain()
        }
    }

    override suspend fun getTransactionStatus(transactionId: String): Result<PaymentResult> {
        return apiCallResponse {
            paymentApi.getTransactionStatus(transactionId)
        }.map {
            it.toDomain()
        }
    }

}
