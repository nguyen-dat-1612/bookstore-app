package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.SearchBooksRequest
import com.dat.bookstore_app.di.qualifier.IODispatcher
import com.dat.bookstore_app.domain.repository.BookRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(request: SearchBooksRequest)
    = bookRepository.getBooks(request)
}