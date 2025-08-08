package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.data.datasource.remote.dto.UserInfoRequestDTO
import com.dat.bookstore_app.network.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getToken(): Flow<String?>
    suspend fun updateProfile(request: UserInfoRequestDTO) : Result<Any?>
    suspend fun updateDeviceToken(request : DeviceTokenRequestDTO) : Result<Any?>
    suspend fun removeDeviceToken(request : DeviceTokenRequestDTO) : Result<Any?>
}