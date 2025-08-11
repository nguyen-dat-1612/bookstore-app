package com.dat.bookstore_app.presentation.features.address

import com.dat.bookstore_app.domain.enums.AddressType
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.models.Commune
import com.dat.bookstore_app.domain.models.Province

data class UpdateAddressUiState (
    val loading: Boolean = false,
    val isUpdateSuccess: Boolean = false,
    val address: Address ?= null,
    val provinces: List<Province> = emptyList(),
    val communes: List<Commune> = emptyList(),
    val chooseProvince: Province? = null,
    val chooseCommune: Commune? = null,
    val addressType: AddressType? = null,
    val isDeleteSuccess: Boolean = false,
)