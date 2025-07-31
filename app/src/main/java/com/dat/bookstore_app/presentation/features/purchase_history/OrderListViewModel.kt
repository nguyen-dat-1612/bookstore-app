package com.dat.bookstore_app.presentation.features.purchase_history

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dat.bookstore_app.data.datasource.remote.dto.SearchOrdersRequest
import com.dat.bookstore_app.data.mapper.OrderPagingSource
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.Order
import com.dat.bookstore_app.domain.usecases.CancelOrderUseCase
import com.dat.bookstore_app.domain.usecases.CreatePaymentUseCase
import com.dat.bookstore_app.domain.usecases.GetPagedOrdersUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val getPagedOrdersUseCase: GetPagedOrdersUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val createPaymentUseCase: CreatePaymentUseCase,
) : BaseViewModel<OrderListState>(){

    override fun initState(): OrderListState = OrderListState()

    val orderPagingFlow = MutableStateFlow<PagingData<Order>>(PagingData.empty())

    fun loadOrders(sort: Sort, filter: OrderStatus) {
        val request = SearchOrdersRequest(
            page = 1,
            size = 10,
            sortBy = sort,
            filter = filter
        )

        updateState { copy(isLoading = true, currentFilter = filter, currentSort = sort) }

        when (val result = getPagedOrdersUseCase(request) { total ->
            updateState { copy(total = total) }
        }) {
            is Result.Success -> {
                viewModelScope.launch {
                    result.data
                        .cachedIn(viewModelScope)
                        .collectLatest {
                            orderPagingFlow.value = it
                            updateState { copy(isLoading = false) }
                        }
                }
            }

            is Result.Error -> {
                updateState { copy(isLoading = false) }
                errorsState.emitError(result.throwable ?: Exception(result.message))
            }
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = cancelOrderUseCase(orderId!!)
            when(result) {
                is Result.Success -> {
                    val currentFilter = uiState.value.currentFilter
                    if (currentFilter == OrderStatus.ALL) {
                        // Chỉ cần reload lại để cập nhật trạng thái đơn hàng
                        loadOrders(uiState.value.currentSort, currentFilter)
                    } else {
                        // Đơn hàng không còn khớp filter hiện tại → biến mất
                        // Force refresh paging data để xóa item
                        loadOrders(uiState.value.currentSort, currentFilter)
                    }
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                }
            }
        }
    }

    fun retryPayment(order: Order) {
        viewModelScope.launch(exceptionHandler) {
            if (order == null) {
                dispatchStateError(IllegalStateException("Order is null"))
                return@launch
            }

            dispatchStateLoading(true)
            try {
                val result = createPaymentUseCase(
                    orderId = order.id,
                    amount = order.totalAmount.toLong(),
                    paymentMethod = order.paymentMethod
                )

                when (result) {
                    is Result.Success -> {

                    }

                    is Result.Error -> {
                        dispatchStateError(e = result.throwable!!)
                    }
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

}