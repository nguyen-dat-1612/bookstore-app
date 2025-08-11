package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.ForgotPasswordRequestDTO
import com.dat.bookstore_app.domain.repository.AuthRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.isValidEmail

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String) : Result<Any?> {
        if (email.isEmpty()) {
            Result.Error(message = "Email cannot be empty", throwable = IllegalArgumentException("Email cannot be empty"))
        }
        if (!email.isValidEmail()) {
            Result.Error(message = "Invalid email format", throwable = IllegalArgumentException("Invalid email format"))
        }
        val request = ForgotPasswordRequestDTO(email)
        return authRepository.forgotPassword(request)
    }

}