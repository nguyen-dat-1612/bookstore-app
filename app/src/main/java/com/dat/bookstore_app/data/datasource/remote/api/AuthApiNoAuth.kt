package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.LoginRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.LoginResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.RegisterRequestDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApiNoAuth {
    @Headers("No-Authentication: true")
    @POST("auth/login")
    suspend fun login(@Body reqLoginDTO: LoginRequestDTO): ApiResponse<LoginResponseDTO>

    @Headers("No-Authentication: true")
    @POST("auth/register")
    suspend fun register(@Body reqUser: RegisterRequestDTO): ApiResponse<LoginResponseDTO>

    @Headers("No-Authentication: true")
    @GET("auth/refresh")
    suspend fun getRefreshToken(): ApiResponse<LoginResponseDTO>
}