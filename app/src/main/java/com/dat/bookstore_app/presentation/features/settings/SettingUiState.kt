package com.dat.bookstore_app.presentation.features.settings

import com.dat.bookstore_app.domain.models.User

data class SettingUiState (
    val LogoutSuccess: Boolean = false,
    val DeleteSuccess: Boolean = false,
    val isChangeSuccess: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val currentToken: String? = null,
    val user: User?= null
)