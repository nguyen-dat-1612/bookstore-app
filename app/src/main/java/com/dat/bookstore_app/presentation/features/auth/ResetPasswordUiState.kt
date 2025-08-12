package com.dat.bookstore_app.presentation.features.auth

data class ResetPasswordUiState (
    val token: String? = null,
    val newPassword: String? = null,
    val confirmPassword: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)