package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.FavoriteRequestDTO
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.models.Favorite
import com.dat.bookstore_app.domain.repository.BookRepository
import com.dat.bookstore_app.domain.repository.FavoriteRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.map
import javax.inject.Inject

class AddFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(bookId: Long) : Result<Favorite> {
        if (bookId < 1) {
            return Result.Error(message = "Invalid book ID", throwable = Exception("Invalid book ID"))
        }
        val reqDTO = FavoriteRequestDTO(bookId)
        return favoriteRepository.createFavorite(reqDTO)
    }
}