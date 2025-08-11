package com.dat.bookstore_app.domain.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AddressType(val displayName: String) : Parcelable {
    HOME("Nhà riêng"),
    OFFICE("Văn phòng");

    override fun toString(): String {
        return displayName
    }
}