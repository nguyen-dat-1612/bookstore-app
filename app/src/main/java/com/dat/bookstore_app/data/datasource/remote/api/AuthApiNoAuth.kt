package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.ForgotPasswordRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.LoginRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.LoginResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.RegisterRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.RegisterResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.ResetPasswordRequestDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiNoAuth {
    @Headers("No-Authentication: true")
    @POST("auth/login")
    suspend fun login(@Body reqLoginDTO: LoginRequestDTO): ApiResponse<LoginResponseDTO>

    @Headers(
        "X-Client-Platform: mobile",
        "No-Authentication: true"
    )
    @POST("auth/register")
    suspend fun register(@Body reqUser: RegisterRequestDTO): ApiResponse<RegisterResponseDTO>

    @Headers("No-Authentication: true")
    @GET("auth/refresh")
    suspend fun getRefreshToken(): ApiResponse<LoginResponseDTO>

    @Headers("No-Authentication: true")
    @POST("auth/outbound/authentication")
    suspend fun outboundAuthentication(@Query("code") code: String): ApiResponse<LoginResponseDTO>

    @Headers(
        "X-Client-Platform: mobile",
        "No-Authentication: true"
    )
    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDTO): ApiResponse<LoginResponseDTO>


    @Headers("No-Authentication: true")
    @GET("auth/reset")
    suspend fun resetRedirect(@Query("token") token: String): ApiResponse<Any?>


    @Headers("No-Authentication: true")
    @POST("auth/reset")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDTO): ApiResponse<Any?>


    @Headers(
        "X-Client-Platform: mobile",
        "No-Authentication: true"
    )
    @POST("auth/resend-verify")
    suspend fun resendVerify(@Query("email") email: String): ApiResponse<Any?>

}