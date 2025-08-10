package com.dat.bookstore_app.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String?,
    val phone: String?,
    val fullName: String?,
    val address: String?,
    val role: String?,
    val id: Long?,
    val avatar: String?,
    val permissions: List<Permission>?,
    val noPassword: Boolean? = false
)
