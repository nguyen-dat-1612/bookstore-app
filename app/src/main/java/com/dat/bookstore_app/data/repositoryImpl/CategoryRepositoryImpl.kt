package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.remote.api.CategoryApi
import com.dat.bookstore_app.data.mapper.toCategoryDomain
import com.dat.bookstore_app.domain.models.Category
import com.dat.bookstore_app.domain.repository.CategoryRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.dat.bookstore_app.utils.extension.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryApi: CategoryApi
): CategoryRepository {
    override suspend fun getCategories(): Result<List<Category>> {
        return apiCallResponse {
            categoryApi.getCategories()
        }.map { it.categories.toCategoryDomain() }
    }
}