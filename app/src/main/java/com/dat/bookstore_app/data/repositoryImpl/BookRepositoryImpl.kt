package com.dat.bookstore_app.data.repositoryImpl

import android.util.Log
import com.dat.bookstore_app.data.datasource.remote.api.BookApi
import com.dat.bookstore_app.data.datasource.remote.dto.BookResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PagedBookResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.SearchBooksRequest
import com.dat.bookstore_app.data.mapper.toDomain
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.models.PagedBook
import com.dat.bookstore_app.domain.repository.BookRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.dat.bookstore_app.utils.extension.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi
): BookRepository {
    override suspend fun getBooks(
        request: SearchBooksRequest
    ): Result<PagedBook> {
        return apiCallResponse {
            val filter = buildFilterString(request)
            bookApi.getBooks(
                page = request.page,
                size = request.pageSize,
                filter = filter.ifBlank { null },
                sort = request.sortBy.queryParam
            )
        }.map { dto ->
            dto.toDomain()
        }
    }

    override suspend fun getBookById(id: Long): Result<Book> {
        return apiCallResponse {
            bookApi.getBookById(id)
        }.map {
            it.toDomain()
        }
    }

    fun buildFilterString(request: SearchBooksRequest): String {
        val conditions = mutableListOf<String>()

        // Title (nếu có)
        request.title?.takeIf { it.isNotBlank() }?.let {
            conditions.add("(title~~'${it}')")
        }

        // Category filter
        if (request.selectedCategoryIds.isNotEmpty()) {
            val categoryFilter = request.selectedCategoryIds
                .joinToString(" or ") { "category.id:'$it'" }
            conditions.add("($categoryFilter)")
        }

        // Price filter
        val priceConditions = mutableListOf<String>()
        request.minPrice?.let { priceConditions.add("price>:$it") }
        request.maxPrice?.let { priceConditions.add("price<:$it") }
        if (priceConditions.isNotEmpty()) {
            conditions.add("(${priceConditions.joinToString(" and ")})")
        }

        val conditionsString = conditions.joinToString(" and ")
        Log.d("buildFilterString", "buildFilterString: $conditionsString")
        // Kết quả cuối cùng
        return conditionsString
    }

}