package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.local.datastore.TokenProvider
import com.dat.bookstore_app.data.datasource.remote.api.AuthApiNoAuth
import com.dat.bookstore_app.data.datasource.remote.api.AuthApiWithAuth
import com.dat.bookstore_app.data.datasource.remote.dto.ChangePasswordRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.LoginRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.LoginResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.RegisterRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.UserLogin
import com.dat.bookstore_app.data.mapper.toDomain
import com.dat.bookstore_app.domain.models.User
import com.dat.bookstore_app.domain.repository.AuthRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.dat.bookstore_app.utils.extension.map
import com.plus.baseandroidapp.data.datasource.datastore.UserManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiWithAuth: AuthApiWithAuth,
    private val authApiNoAuth: AuthApiNoAuth,
    private val tokenProvider: TokenProvider,
    private val userManager: UserManager
) : AuthRepository {
    override suspend fun login(username: String, password: String): Result<LoginResponseDTO> {
        return apiCallResponse {
            val response = authApiNoAuth.login(LoginRequestDTO(username, password))
            tokenProvider.updateToken(response.data!!.accessToken);
            userManager.saveUser(response.data.user.toDomain())
            response
        }
    }

    override suspend fun register(reqUser: RegisterRequestDTO): Result<LoginResponseDTO> {
        return apiCallResponse {
            val response = authApiNoAuth.register(reqUser)
            tokenProvider.updateToken(response.data!!.accessToken);
            userManager.saveUser(response.data.user.toDomain())
            response
        }
    }

    override suspend fun getAccount(): Result<User> {
        return apiCallResponse {
            val response = authApiWithAuth.getAccount()
            userManager.saveUser(response.data?.user!!.toDomain())
            response
        }.map {
            it.user.toDomain()
        }
    }

    override suspend fun logout(): Result<Any?> {
        return apiCallResponse {
            val response = authApiWithAuth.logout()
            tokenProvider.clearToken()
            userManager.clearUser()
            response
        }
    }

    override suspend fun getRefreshToken(): Result<LoginResponseDTO> {
        return apiCallResponse {
            val response = authApiNoAuth.getRefreshToken()
            tokenProvider.updateToken(response.data!!.accessToken);
            response
        }
    }

    override suspend fun changePassword(changePasswordRequestDTO: ChangePasswordRequestDTO): Result<Any?> {
        return apiCallResponse {
            val response = authApiWithAuth.changePassword(changePasswordRequestDTO)
            response
        }
    }

    override suspend fun getUser(): Result<UserLogin> {
        return userManager.getUser().fold(
            onSuccess = { Result.Success(it) },
            onFailure = { Result.Error(message = it.message ?: "Unknown error", throwable = it) }
        )
    }
}