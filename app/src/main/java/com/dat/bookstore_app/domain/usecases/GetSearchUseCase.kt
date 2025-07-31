package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.SearchRepository
import com.dat.bookstore_app.network.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke() : Flow<Result<List<String>>> {
        return searchRepository.getSearchHistory()
    }
}