package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.models.Favorite
import com.dat.bookstore_app.domain.repository.FavoriteRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class CheckFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Favorite?> {
        if (bookId <= 0) {
            return Result.Error(message = "Invalid book ID", throwable = IllegalArgumentException("Invalid book ID"))
        }
        return favoriteRepository.checkFavorite(bookId)
    }
}