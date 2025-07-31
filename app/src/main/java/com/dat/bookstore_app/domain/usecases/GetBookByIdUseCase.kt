package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.BookRepository
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val bookRepository: BookRepository
){
    suspend operator fun invoke(id: Long) = bookRepository.getBookById(id)
}