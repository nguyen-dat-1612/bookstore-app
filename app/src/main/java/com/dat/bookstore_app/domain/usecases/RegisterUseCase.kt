package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.LoginResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.RegisterRequestDTO
import com.dat.bookstore_app.domain.repository.AuthRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        phone: String,
        password: String
    ): Result<LoginResponseDTO> {
        // Tên
        if (fullName.isBlank()) {
            return Result.Error(
                message = "Tên không được để trống",
                throwable = IllegalArgumentException("Full name is blank")
            )
        }

        // Số điện thoại
        if (phone.isBlank()) {
            return Result.Error(
                message = "Số điện thoại không được để trống",
                throwable = IllegalArgumentException("Phone number is blank")
            )
        } else if (!phone.matches(Regex("^\\d{9,11}$"))) {
            return Result.Error(
                message = "Số điện thoại không hợp lệ",
                throwable = IllegalArgumentException("Phone number invalid format")
            )
        }

        // Email
        if (email.isBlank()) {
            return Result.Error(
                message = "Email không được để trống",
                throwable = IllegalArgumentException("Email is blank")
            )
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.Error(
                message = "Email không hợp lệ",
                throwable = IllegalArgumentException("Invalid email")
            )
        }

        // Mật khẩu
        if (password.isBlank()) {
            return Result.Error(
                message = "Mật khẩu không được để trống",
                throwable = IllegalArgumentException("Password is blank")
            )
        } else if (password.length < 8) {
            return Result.Error(
                message = "Mật khẩu phải từ 8 ký tự trở lên",
                throwable = IllegalArgumentException("Weak password")
            )
        }

        // Nếu hợp lệ → gọi API
        return authRepository.register(
            RegisterRequestDTO(
                fullName = fullName,
                email = email,
                phone = phone,
                password = password
            )
        )
    }
}
