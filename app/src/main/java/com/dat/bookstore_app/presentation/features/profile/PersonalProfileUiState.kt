package com.dat.bookstore_app.presentation.features.profile

import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.models.User

data class PersonalProfileUiState(
    val user: User? = null,
    val isUpdateProfileSuccess: Boolean = false,
    val isLoadingUpdateProfile: Boolean = false,
    val addressList: List<Address> = emptyList()
)
