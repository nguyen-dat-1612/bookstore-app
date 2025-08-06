package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.remote.api.FavoriteApi
import com.dat.bookstore_app.data.datasource.remote.dto.FavoriteRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.FavoriteResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.GetFavoriteRequest
import com.dat.bookstore_app.data.datasource.remote.dto.PagedFavoriteResponseDTO
import com.dat.bookstore_app.data.mapper.toDomain
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.Favorite
import com.dat.bookstore_app.domain.models.PagedFavorite
import com.dat.bookstore_app.domain.repository.FavoriteRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.dat.bookstore_app.utils.extension.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteApi: FavoriteApi
) : FavoriteRepository {

    override suspend fun getFavorites(
        request: GetFavoriteRequest
    ): Result<PagedFavorite> {
        return apiCallResponse {
            with(request) {
                favoriteApi.getFavorites(
                    page = page,
                    size = size,
                    sort = sort.queryParam
                )
            }
        }.map {
            it.toDomain()
        }
    }

    override suspend fun createFavorite(reqDTO: FavoriteRequestDTO): Result<Favorite> {
        return apiCallResponse {
            favoriteApi.createFavorite(reqDTO)
        }.map {
            it.toDomain()
        }
    }

    override suspend fun deleteFavorite(id: Long): Result<Any?> {
        return apiCallResponse {
            favoriteApi.deleteFavorite(id)
        }
    }

    override suspend fun checkFavorite(id: Long): Result<Favorite?> {
        return try {
            val response = favoriteApi.checkFavorite(id)
            Result.Success(response.data?.toDomain())
        } catch (e: Exception) {
            Result.Error(message = e.message ?: "Unknown error", throwable = e)
        }
    }
}