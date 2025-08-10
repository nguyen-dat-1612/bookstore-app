package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.CreatePasswordRequestDTO
import com.dat.bookstore_app.domain.repository.UserRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class CreatePasswordUseCase @Inject  constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(password: String, confirmPassword: String) : Result<Any?> {
        if (password.isBlank() || confirmPassword.isBlank()) {
            return Result.Error(message = "New password and confirm password cannot be empty")
        }
        if (password != confirmPassword) {
            return Result.Error(message = "New password and confirm password must be the same")
        }
        val request = CreatePasswordRequestDTO(password, confirmPassword)
        return userRepository.createPassword(request)
    }

}