package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.SearchRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class SaveSearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<Unit> {
        if (query.isNotBlank()) {
            return searchRepository.saveSearchQuery(query)
        }
        return Result.Error(message = "Invalid query", throwable = Exception("Invalid query"))
    }
}