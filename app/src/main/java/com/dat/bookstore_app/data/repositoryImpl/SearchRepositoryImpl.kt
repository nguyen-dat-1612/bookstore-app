package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.local.datastore.SearchHistoryManager
import com.dat.bookstore_app.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl @Inject constructor(
    private val searchHistoryManager: SearchHistoryManager
): SearchRepository {
    override suspend fun saveSearchQuery(query: String) : Result<Unit>{
        return try {
            searchHistoryManager.saveSearchQuery(query)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(throwable = e, message = "Load failed")
        }
    }

    override fun getSearchHistory(): Flow<Result<List<String>>> {
        return searchHistoryManager.getSearchHistory()
            .map { Result.Success(it) as Result<List<String>> }
            .catch { emit(Result.Error(throwable = it, message = "Load failed")) }
    }

    override suspend fun clearHistory(): Result<Unit> {
        return try {
            searchHistoryManager.clearHistory()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(throwable = e, message = "Clear failed")
        }
    }
}
