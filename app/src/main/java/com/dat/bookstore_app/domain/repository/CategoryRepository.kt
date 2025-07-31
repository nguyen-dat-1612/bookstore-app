package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.domain.models.Category
import com.dat.bookstore_app.network.Result

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
}