package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.AddressRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.AddressResponseDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AddressApi {
    @GET("addresses/me")
    suspend fun getAddresses(): ApiResponse<List<AddressResponseDTO>>

    @POST("addresses")
    suspend fun createAddress(@Body request: AddressRequestDTO): ApiResponse<AddressResponseDTO>

    @PUT("addresses/{id}")
    suspend fun updateAddress(@Path("id") id: Long, @Body request: AddressRequestDTO): ApiResponse<AddressResponseDTO>

    @DELETE("addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: Long): ApiResponse<Any?>

    @PUT("addresses/{id}/default")
    suspend fun setDefaultAddress(@Path("id") id: Long): ApiResponse<AddressResponseDTO>
}