package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.data.datasource.remote.dto.CreateOrderRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.SearchOrdersRequest
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.models.PagedOrder
import com.dat.bookstore_app.network.Result

interface OrderRepository {
    suspend fun createOrder(reqDTO: CreateOrderRequestDTO): Result<Order>
    suspend fun getOrderHistory(searchOrderRequest: SearchOrdersRequest): Result<PagedOrder>
    suspend fun getOrderById(id: Long): Result<Order>
    suspend fun cancelOrder(orderId: Long): Result<Any?>
}