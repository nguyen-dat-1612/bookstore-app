package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.AuthRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.isValidEmail

class ResendVerifyUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Any?> {
        if (email.isEmpty()) {
            return Result.Error(
                message = "Email không được để trống",
                throwable = IllegalArgumentException("Email không được để trống")
            )
        }
        if (!email.isValidEmail()) {
            return Result.Error(
                message = "Email không hợp lệ",
                throwable = IllegalArgumentException("Email không hợp lệ")
            )
        }
        return authRepository.resendVerifyEmail(email)
    }
}