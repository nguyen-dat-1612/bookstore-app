package com.dat.bookstore_app.domain.models

import android.os.Parcelable
import com.dat.bookstore_app.domain.enums.AddressType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val id: Long?,
    val fullName: String?,
    val phoneNumber: String?,
    val province: String?,
    val ward: String?,
    val addressDetail: String?,
    val addressType: AddressType?,
    val isDefault: Boolean,
    val createdAt: String?,
    val updatedAt: String
) : Parcelable
