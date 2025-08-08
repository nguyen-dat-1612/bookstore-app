package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.repository.BookRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val bookRepository: BookRepository
){
    suspend operator fun invoke(id: Long) : Result<Book> {
        return bookRepository.getBookById(id)
    }
}