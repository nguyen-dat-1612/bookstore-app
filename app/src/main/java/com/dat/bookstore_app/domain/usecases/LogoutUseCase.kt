package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.AuthRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Any?> {
        return authRepository.logout()
    }
}