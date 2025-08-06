package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.FavoriteRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class DeleteFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(favoriteId: Long) : Result<Any?> {
        if (favoriteId < 1) {
            return Result.Error(message = "Invalid favorite ID", throwable = Exception("Invalid favorite ID"))
        }
       return favoriteRepository.deleteFavorite(favoriteId)
    }
}