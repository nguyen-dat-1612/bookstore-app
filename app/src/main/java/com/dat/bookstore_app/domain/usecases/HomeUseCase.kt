package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.SearchBooksRequest
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.HomeBooksResult
import com.dat.bookstore_app.domain.repository.BookRepository
import com.dat.bookstore_app.network.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class HomeUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend fun loadHomeBooks(): Result<HomeBooksResult> = coroutineScope {
        try {
            val popularDeferred = async {
                val request = SearchBooksRequest(
                    selectedCategoryIds = emptyList(),
                    minPrice = null,
                    maxPrice = null,
                    sortBy = Sort.SOLD_DESC,
                    page = 1,
                    pageSize = 7
                )
                bookRepository.getBooks(request)
            }
            val newDeferred = async {
                val request = SearchBooksRequest(
                    selectedCategoryIds = emptyList(),
                    minPrice = null,
                    maxPrice = null,
                    sortBy = Sort.NEW_DESC,
                    page = 1,
                    pageSize = 7
                )
                bookRepository.getBooks(request)
            }

            val lowPriceDeferred = async {
                val request = SearchBooksRequest(
                    selectedCategoryIds = emptyList(),
                    minPrice = null,
                    maxPrice = null,
                    sortBy = Sort.PRICE_ASC,
                    page = 1,
                    pageSize = 7
                )
                bookRepository.getBooks(request)
            }

            val highPriceDeferred = async {
                val request = SearchBooksRequest(
                    selectedCategoryIds = emptyList(),
                    minPrice = null,
                    maxPrice = null,
                    sortBy = Sort.PRICE_DESC,
                    page = 1,
                    pageSize = 7
                )
                bookRepository.getBooks(request)
            }

            val popularResult = popularDeferred.await()
            val newResult = newDeferred.await()
            val lowPriceResult = lowPriceDeferred.await()
            val highPriceResult = highPriceDeferred.await()

            if (popularResult is Result.Success &&
                newResult is Result.Success &&
                lowPriceResult is Result.Success &&
                highPriceResult is Result.Success
            ) {
                Result.Success(
                    HomeBooksResult(
                        popularBooks = popularResult.data,
                        newBooks = newResult.data,
                        lowToHighPriceBooks = lowPriceResult.data,
                        highToLowPriceBooks = highPriceResult.data
                    )
                )
            } else {
                when {
                    popularResult is Result.Error ->Result.Error(message = "Failed to load popular books", throwable = popularResult.throwable)
                    newResult is Result.Error -> Result.Error(message = "Failed to load new books", throwable = newResult.throwable)
                    else -> {
                       Result.Error(message = "Failed to load home books", throwable = null)
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(message = "Failed to load home books", throwable = e)
        }
    }


}