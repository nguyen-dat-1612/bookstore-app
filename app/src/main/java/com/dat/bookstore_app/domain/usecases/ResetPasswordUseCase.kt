package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.ResetPasswordRequestDTO
import com.dat.bookstore_app.domain.repository.AuthRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String, newPassword: String, confirmPassword: String) : Result<Any?> {
        if (token.isEmpty()) {
            return Result.Error(message = "Token is empty", throwable = IllegalArgumentException("Token is empty"))
        }
        if (newPassword.isEmpty()) {
            return Result.Error(message = "New password is empty", throwable = IllegalArgumentException("New password is empty"))
        }
        if (confirmPassword.isEmpty()) {
            return Result.Error(message = "Confirm password is empty", throwable = IllegalArgumentException("Confirm password is empty"))
        }
        if (newPassword != confirmPassword) {
            return Result.Error(message = "New password and confirm password are not the same", throwable = IllegalArgumentException("New password and confirm password are not the same"))
        }
        val result = ResetPasswordRequestDTO(token, newPassword, confirmPassword)
        return authRepository.resetPassword(result)
    }


}