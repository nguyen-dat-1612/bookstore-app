package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.LoginResponseDTO
import com.dat.bookstore_app.domain.repository.AuthRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class LoginGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(code: String): Result<LoginResponseDTO> {
        if (code.isEmpty()) {
            return Result.Error(message = "Code is empty", throwable = Exception("Code is empty"))
        }
        return authRepository.outboundAuthentication(code)
    }
}