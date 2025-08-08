package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.DeviceTokenRequestDTO
import com.dat.bookstore_app.domain.repository.UserRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
class UpdateTokenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long, deviceToken: String) : Result<Any?> {
        if (userId == 0L) return Result.Error(message = "User ID is required", throwable = Exception("User ID is required"))
        val result = DeviceTokenRequestDTO(userId, deviceToken)
        return userRepository.updateDeviceToken(result)
    }
}