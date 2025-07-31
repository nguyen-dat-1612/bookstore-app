package com.dat.bookstore_app.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Permission(
    val name: String,
    val path: String,
    val method: String,
    val module: String
)