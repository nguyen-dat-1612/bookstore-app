package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.DeviceTokenRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.UserInfoRequestDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApi {
    @PUT("users/info")
    suspend fun updateUserInfo(@Body request: UserInfoRequestDTO): ApiResponse<Any?>

    @POST("users/device-token")
    suspend fun updateDeviceToken(@Body request: DeviceTokenRequestDTO): ApiResponse<Any?>

    @POST("users/remove-device-token")
    suspend fun removeDeviceToken(@Body request: DeviceTokenRequestDTO): ApiResponse<Any?>
}