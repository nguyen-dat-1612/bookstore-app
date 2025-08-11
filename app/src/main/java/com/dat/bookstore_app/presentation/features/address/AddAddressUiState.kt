package com.dat.bookstore_app.presentation.features.address

import com.dat.bookstore_app.domain.enums.AddressType
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.models.Commune
import com.dat.bookstore_app.domain.models.Province

data class AddAddressUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val fullName: String = "",
    val phoneNumber: String = "",
    val province: String = "",
    val ward: String = "",
    val addressDetail: String = "",
    val addressType: AddressType = AddressType.HOME,
    val isDefault: Boolean = false,
    val provinces: List<Province> = emptyList(),
    val communes: List<Commune> = emptyList(),
    val chooseProvince: Province?= null,
    val chooseCommune: Commune?= null,
    val address: Address? = null
)