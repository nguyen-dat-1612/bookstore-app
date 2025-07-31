package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.CartRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.CartResponseDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApi {
    @GET("carts")
    suspend fun getCart(): ApiResponse<List<CartResponseDTO>>

    @POST("carts")
    suspend fun addToCart(@Body reqDTO: CartRequestDTO): ApiResponse<CartResponseDTO>


    @DELETE("carts/{bookId}")
    suspend fun deleteFromCart(@Path("bookId") id: Long): ApiResponse<Any?>


    @PUT("carts")
    suspend fun updateCart(@Body reqDTO: CartRequestDTO): ApiResponse<CartResponseDTO>

    @DELETE("carts/clear")
    suspend fun clearCart(): ApiResponse<Any?>

}