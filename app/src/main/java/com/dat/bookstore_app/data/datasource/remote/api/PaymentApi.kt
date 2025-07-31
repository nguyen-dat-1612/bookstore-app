package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.PaymentRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PaymentResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PaymentResultDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApi {

    @POST("payments")
    suspend fun createPayment(@Body dto: PaymentRequestDTO): ApiResponse<PaymentResponseDTO>

    @GET("transactions/{transactionId}")
    suspend fun getTransactionStatus(@Path("transactionId") transactionId: String): ApiResponse<PaymentResultDTO>
}