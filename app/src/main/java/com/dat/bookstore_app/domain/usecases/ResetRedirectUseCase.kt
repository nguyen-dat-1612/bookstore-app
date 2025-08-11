package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.AuthRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class ResetRedirectUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String): Result<Any?> {
        if (token.isEmpty()) {
            return Result.Error(message = "Token is empty", throwable = IllegalArgumentException("Token is empty"))
        }
        return authRepository.resetRedirect(token)
    }
}