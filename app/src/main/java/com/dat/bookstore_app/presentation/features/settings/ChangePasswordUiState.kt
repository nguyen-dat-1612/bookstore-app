package com.dat.bookstore_app.presentation.features.settings

data class ChangePasswordUiState (
    val isChangePasswordSuccess: Boolean = false,
    val messageError: String? = null
)