package com.dat.bookstore_app.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dat.bookstore_app.data.datasource.remote.dto.SearchOrdersRequest
import com.dat.bookstore_app.data.mapper.OrderPagingSource
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.models.PagedOrder
import com.dat.bookstore_app.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class GetPagedOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(
        request: SearchOrdersRequest,
        onTotalChanged: (Int) -> Unit
    ): Result<Flow<PagingData<Order>>> {
        return try {
            val pager = Pager(
                config = PagingConfig(pageSize = request.size),
                pagingSourceFactory = {
                    OrderPagingSource(request = request, fetch ={ pagedRequest ->
                        orderRepository.getOrderHistory(pagedRequest)
                    }, onTotalReceived = {
                        onTotalChanged(it)
                    })
                }
            ).flow
            Result.Success(pager)
        } catch (e: Exception) {
            Result.Error(message = "Paging setup failed", throwable = e)
        }
    }
}