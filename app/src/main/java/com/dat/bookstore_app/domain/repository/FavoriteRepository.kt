package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.data.datasource.remote.dto.FavoriteRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.FavoriteResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.GetFavoriteRequest
import com.dat.bookstore_app.data.datasource.remote.dto.PagedFavoriteResponseDTO
import com.dat.bookstore_app.domain.models.Favorite
import com.dat.bookstore_app.domain.models.PagedFavorite
import com.dat.bookstore_app.network.Result

interface FavoriteRepository {
    suspend fun getFavorites(request: GetFavoriteRequest): Result<PagedFavorite>
    suspend fun createFavorite(reqDTO: FavoriteRequestDTO): Result<Favorite>
    suspend fun deleteFavorite(id: Long): Result<Any?>
    suspend fun checkFavorite(id: Long): Result<Favorite?>
}