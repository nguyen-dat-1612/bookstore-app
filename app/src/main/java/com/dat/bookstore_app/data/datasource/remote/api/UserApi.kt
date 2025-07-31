package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.UserInfoRequestDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.PUT

interface UserApi {
    @PUT("users/info")
    suspend fun updateUserInfo(@Body request: UserInfoRequestDTO): ApiResponse<Any?>
}