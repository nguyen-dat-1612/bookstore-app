package com.dat.bookstore_app.presentation.features.auth

data class LoginUiState(
    val isLoading: Boolean = false,
    val email: String = "user@gmail.com",
    val password: String = "123456",
    val isSuccess: Boolean = false
)