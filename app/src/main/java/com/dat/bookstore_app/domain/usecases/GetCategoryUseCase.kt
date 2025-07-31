package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke() = categoryRepository.getCategories()
}