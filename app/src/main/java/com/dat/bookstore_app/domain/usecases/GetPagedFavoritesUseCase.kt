package com.dat.bookstore_app.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dat.bookstore_app.data.datasource.remote.dto.GetFavoriteRequest
import com.dat.bookstore_app.data.mapper.FavoritePagingSource
import com.dat.bookstore_app.domain.models.Favorite
import com.dat.bookstore_app.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class GetPagedFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(
        request: GetFavoriteRequest,
        onTotalChanged: (Int) -> Unit
    ) : Result<Flow<PagingData<Favorite>>>  {
        return try {
            val pager = Pager(
                config = PagingConfig(pageSize = request.size),
                pagingSourceFactory = {
                    FavoritePagingSource(request = request, fetch = { pagedRequest ->
                        val response = favoriteRepository.getFavorites(pagedRequest)
                        response
                    }, onTotalReceived = {
                        onTotalChanged(it)
                    })
                }
            ).flow
            Result.Success(pager)
        } catch (e: Exception) {
            Result.Error(
                message = "Paging setup failed",
                throwable = e
            )
        }
    }

}