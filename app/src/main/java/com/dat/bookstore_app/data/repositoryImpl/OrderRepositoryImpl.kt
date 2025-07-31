package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.remote.api.OrderApi
import com.dat.bookstore_app.data.datasource.remote.dto.CreateOrderRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.SearchOrdersRequest
import com.dat.bookstore_app.data.mapper.toDomain
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.models.PagedOrder
import com.dat.bookstore_app.domain.repository.OrderRepository
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.dat.bookstore_app.utils.extension.map
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderApi: OrderApi
) : OrderRepository{
    override suspend fun createOrder(reqDTO: CreateOrderRequestDTO): Result<Order> {
        return apiCallResponse {
            orderApi.createOrder(reqDTO)
        }.map {
            it.toDomain()
        }
    }

    override suspend fun getOrderHistory(searchOrderRequest: SearchOrdersRequest): Result<PagedOrder> {
        val reposonse = apiCallResponse {
            val filterParam = searchOrderRequest.filter?.takeIf { it != OrderStatus.ALL }
                ?.let { "status ~ '${it}'" }
            orderApi.getOrderHistory(
                page = searchOrderRequest.page,
                size = searchOrderRequest.size,
                sort = searchOrderRequest.sortBy.queryParam,
                filter = filterParam
            )
        }.map {
            it.toDomain()
        }
        return reposonse
    }

    override suspend fun getOrderById(id: Long): Result<Order> {
        return apiCallResponse {
            orderApi.getOrderById(id)
        }.map {
            it.toDomain()
        }
    }

    override suspend fun cancelOrder(orderId: Long): Result<Any?> {
        return apiCallResponse {
            orderApi.cancelOrder(orderId)
        }
    }

}