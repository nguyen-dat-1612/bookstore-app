package com.dat.bookstore_app.presentation.features.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.GetOrderByIdUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderSuccessViewModel @Inject constructor(
    private val orderByIdUseCase: GetOrderByIdUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<OrderSuccessUiState>(){

    override fun initState() = OrderSuccessUiState()

    val orderId = savedStateHandle.get<Long>("orderId") ?: 0L

    fun loadOrder() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = orderByIdUseCase(orderId)
            when (result) {
                is Result.Success -> {
                    updateState {
                        copy(order = result.data)
                    }
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)

                }
            }
            dispatchStateLoading(false)
        }
    }
}