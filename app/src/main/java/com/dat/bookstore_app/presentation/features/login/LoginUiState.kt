package com.dat.bookstore_app.presentation.features.login

data class LoginUiState(
    val email: String = "user@gmail.com",
    val password: String = "123456",
    val isSuccess: Boolean = false
)