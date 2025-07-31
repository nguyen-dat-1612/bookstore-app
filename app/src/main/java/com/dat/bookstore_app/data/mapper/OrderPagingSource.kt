package com.dat.bookstore_app.data.mapper

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dat.bookstore_app.data.datasource.remote.dto.SearchOrdersRequest
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.models.PagedOrder
import com.dat.bookstore_app.network.Result

class OrderPagingSource (
    private val request: SearchOrdersRequest,
    private val fetch: suspend (SearchOrdersRequest) -> Result<PagedOrder>,
    private val onTotalReceived: ((Int) -> Unit)? = null
) : PagingSource<Int, Order>() {
    override fun getRefreshKey(state: PagingState<Int, Order>): Int? {
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Order> {
        val page = params.key ?: 1

        val updatedRequest = request.copy(page = page)

        return when (val result = fetch(updatedRequest)) {
            is Result.Success -> {
                val data = result.data
                onTotalReceived?.invoke(data.total)
                LoadResult.Page(
                    data = data.orders,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page >= data.pages) null else page + 1
                )
            }
            is Result.Error -> LoadResult.Error(result.throwable ?: Exception(result.message))

        }
    }
}