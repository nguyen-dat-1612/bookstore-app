package com.dat.bookstore_app.presentation.features.settings

data class SettingUiState (
    val LogoutSuccess: Boolean = false,
    val DeleteSuccess: Boolean = false,
    val isChangeSuccess: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val currentToken: String? = null
)