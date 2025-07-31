package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.SearchBooksRequest
import com.dat.bookstore_app.domain.repository.BookRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
class GetBooksTotalOnlyUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    suspend operator fun invoke(request: SearchBooksRequest): Result<Int> {
        return when (val result = repository.getBooks(request)) {
            is Result.Success -> Result.Success(result.data.total)
            is Result.Error -> Result.Error(message = result.message, throwable = result.throwable)
        }
    }
}