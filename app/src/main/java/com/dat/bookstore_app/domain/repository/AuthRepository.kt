package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.data.datasource.remote.dto.ChangePasswordRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.LoginResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.RegisterRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.RegisterResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.UserLogin
import com.dat.bookstore_app.domain.models.User
import com.dat.bookstore_app.network.ApiResponse
import com.dat.bookstore_app.network.Result

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginResponseDTO>
    suspend fun register(reqUser: RegisterRequestDTO): Result<RegisterResponseDTO>
    suspend fun getAccount(): Result<User>
    suspend fun logout(): Result<Any?>
    suspend fun getRefreshToken(): Result<LoginResponseDTO>
    suspend fun changePassword(changePasswordRequestDTO: ChangePasswordRequestDTO): Result<Any?>
    suspend fun getUser(): Result<UserLogin>
}