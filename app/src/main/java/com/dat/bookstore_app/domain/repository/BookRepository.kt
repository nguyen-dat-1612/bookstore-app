package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.data.datasource.remote.dto.SearchBooksRequest
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.models.PagedBook
import com.dat.bookstore_app.network.Result

interface BookRepository {
    suspend fun getBooks(request: SearchBooksRequest): Result<PagedBook>
    suspend fun getBookById(id: Long): Result<Book>
}