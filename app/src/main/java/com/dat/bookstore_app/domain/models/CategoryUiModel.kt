package com.dat.bookstore_app.domain.models

data class CategoryUiModel(
    val id: Long,
    val name: String,
    var isSelected: Boolean = false
)