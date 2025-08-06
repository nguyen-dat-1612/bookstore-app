package com.dat.bookstore_app.data.mapper

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dat.bookstore_app.data.datasource.remote.dto.GetFavoriteRequest
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.models.Favorite
import com.dat.bookstore_app.domain.models.PagedFavorite
import com.dat.bookstore_app.network.Result

class FavoritePagingSource (
    private val request: GetFavoriteRequest,
    private val fetch: suspend (GetFavoriteRequest) -> Result<PagedFavorite>,
    private val onTotalReceived: ((Int) -> Unit)? = null

) : PagingSource<Int, Favorite>() {

    override fun getRefreshKey(state: PagingState<Int, Favorite>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Favorite> {

        val page = params.key ?: 1

        val updatedRequest = request.copy(page = page)

        return when (val result = fetch(updatedRequest)) {
            is Result.Success -> {
                val data = result.data
                onTotalReceived?.invoke(data.total)
                LoadResult.Page(
                    data = data.favorites,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page >= data.pages) null else page + 1
                )
            }

            is Result.Error -> LoadResult.Error(result.throwable ?: Exception(result.message))
        }
    }


}