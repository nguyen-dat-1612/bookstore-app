package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.network.Result
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun saveSearchQuery(query: String): Result<Unit>
    fun getSearchHistory(): Flow<Result<List<String>>>
    suspend fun clearHistory(): Result<Unit>
}