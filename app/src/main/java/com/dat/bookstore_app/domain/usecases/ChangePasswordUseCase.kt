package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.ChangePasswordRequestDTO
import com.dat.bookstore_app.domain.repository.AuthRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    suspend operator fun invoke(currentPassword: String, newPassword: String): Result<Any?> {

        if (currentPassword.isBlank() || newPassword.isBlank()) {
            return Result.Error(message = "Current password and new password cannot be empty")
        }
        if (currentPassword == newPassword) {
            return Result.Error(message = "Current password and new password cannot be the same")
        }

        val result = authRepository.getUser()

        return when (result) {
            is Result.Success -> {
                val user = result.data
                val response = authRepository.changePassword(ChangePasswordRequestDTO(user.id, currentPassword, newPassword))
                when (response) {
                    is Result.Success -> Result.Success(response.data)
                    is Result.Error ->
                        if (response.code == 400) {
                            Result.Error(message = "Cập nhật mật khẩu mới thất bại")
                        } else {
                            Result.Error(message = response.message, throwable = response.throwable)
                        }
                }
            }
            is Result.Error -> {
                Result.Error(message = result.message, throwable = result.throwable)
            }
        }
    }

}