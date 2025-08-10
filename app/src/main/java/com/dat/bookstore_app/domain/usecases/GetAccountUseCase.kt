package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.models.User
import com.dat.bookstore_app.domain.repository.AuthRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class GetAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() : Result<User> {
        return authRepository.getAccount()
    }
}