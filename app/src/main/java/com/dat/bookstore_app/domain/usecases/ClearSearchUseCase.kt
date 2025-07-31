package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.SearchRepository
import javax.inject.Inject

class ClearSearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke() = searchRepository.clearHistory()
}