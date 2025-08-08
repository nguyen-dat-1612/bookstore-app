package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.CancelOrderResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.CreateOrderRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.OrderResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PagedOrderResponseDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {

    @Headers("X-Client-Platform: mobile")
    @POST("orders")
    suspend fun createOrder(@Body reqDTO: CreateOrderRequestDTO): ApiResponse<OrderResponseDTO>

    @GET("orders/history")
    suspend fun getOrderHistory(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String = "updatedAt,desc",
        @Query("filter") filter: String? = null
    ): ApiResponse<PagedOrderResponseDTO>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: Long): ApiResponse<OrderResponseDTO>

    @PUT("orders/{orderId}/cancel")
    suspend fun cancelOrder(@Path("orderId") orderId: Long): ApiResponse<CancelOrderResponseDTO>
}