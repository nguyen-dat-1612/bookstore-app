package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.ChangePasswordRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.UserGetAccount
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiWithAuth {
    @GET("auth/account")
    suspend fun getAccount(): ApiResponse<UserGetAccount>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Any?>

    @POST("auth/change-password")
    suspend fun changePassword(@Body reqChangePasswordDTO: ChangePasswordRequestDTO): ApiResponse<Any?>


}