package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.remote.api.UserApi
import com.dat.bookstore_app.data.datasource.remote.dto.DeviceTokenRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.UserInfoRequestDTO
import com.dat.bookstore_app.domain.repository.UserRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.plus.baseandroidapp.data.datasource.datastore.UserManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userManager: UserManager,
    private val userApi: UserApi
) : UserRepository {
    override fun getToken(): Flow<String?> {
        return userManager.getAccessToken()
    }

    override suspend fun updateProfile(request: UserInfoRequestDTO): Result<Any?> {
        return apiCallResponse {
            userApi.updateUserInfo(request)
        }
    }

    override suspend fun updateDeviceToken(request: DeviceTokenRequestDTO): Result<Any?> {
        return apiCallResponse {
            userApi.updateDeviceToken(request)
        }
    }

    override suspend fun removeDeviceToken(request: DeviceTokenRequestDTO): Result<Any?> {
        return apiCallResponse {
            userApi.removeDeviceToken(request)
        }
    }
}