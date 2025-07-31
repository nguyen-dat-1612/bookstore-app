package com.dat.bookstore_app.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dat.bookstore_app.data.datasource.remote.dto.SearchBooksRequest
import com.dat.bookstore_app.data.mapper.BookPagingSource
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class GetPagedBooksUseCase @Inject constructor(
    private val repository: BookRepository,
) {
    operator fun invoke(
        request: SearchBooksRequest,
        onTotalChanged: (Int) -> Unit
    ): Result<Flow<PagingData<Book>>> {
        return try {
            val pager = Pager(
                config = PagingConfig(pageSize = request.pageSize),
                pagingSourceFactory = {
                    BookPagingSource(request = request, fetch ={ pagedRequest ->
                        val response = repository.getBooks(pagedRequest)
                        response
                    }, onTotalReceived = {
                        onTotalChanged(it)
                    })
                }
            ).flow

            Result.Success(pager)
        } catch (e: Exception) {
            Result.Error(message = "Paging setup failed", throwable = e)
        }
    }
}