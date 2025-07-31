package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.AuthRepository
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.getAccount()
}