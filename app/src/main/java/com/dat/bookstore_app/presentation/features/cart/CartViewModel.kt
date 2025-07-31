package com.dat.bookstore_app.presentation.features.cart

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.data.datasource.remote.dto.DeleteAllCartUseCase
import com.dat.bookstore_app.data.datasource.remote.dto.DeleteOneCartUseCase
import com.dat.bookstore_app.data.datasource.remote.dto.UpdateToCartUseCase
import com.dat.bookstore_app.domain.usecases.GetCartUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val updateToCartUseCase: UpdateToCartUseCase,
    private val deleteOneCartUseCase: DeleteOneCartUseCase,
    private val deleteAllCartUseCase: DeleteAllCartUseCase
) : BaseViewModel<CartUiState>(){

    override fun initState() = CartUiState()

    fun loadCart() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            when (val result = getCartUseCase()) {
                is Result.Success -> {
                    updateState { copy(ListCart = result.data) }
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                }
            }
            dispatchStateLoading(false)
        }
    }
    fun updateCart(bookId: Long, quantity: Int) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = updateToCartUseCase(bookId, quantity)
            when (result) {
                is Result.Success -> {
                    updateState {
                        copy(ListCart = ListCart.map { cartItem ->
                            cartItem.copy(
                                quantity = if (cartItem.book.id == bookId) result.data.quantity else cartItem.quantity
                            )
                        })
                    }
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                }
            }
            dispatchStateLoading(false)
        }
    }

    fun deleteFromCart(bookId: Long) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = deleteOneCartUseCase(bookId)
            when (result) {
                is Result.Success -> {
                    updateState {
                        copy(ListCart = ListCart.filter { it.book.id != bookId })
                    }
                }

                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                }
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = deleteAllCartUseCase()
            when (result) {
                is Result.Success -> {
                    updateState {
                        copy(ListCart = emptyList())
                    }
                }

                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                }
            }
            dispatchStateLoading(false)
        }
    }

    fun updateChecked(bookId: Long, isChecked: Boolean) {
        updateState {
            copy(ListCart = ListCart.map { cartItem ->
                cartItem.copy(
                    isSelected = if (cartItem.book.id == bookId) isChecked else cartItem.isSelected
                )
            })
        }
    }

    fun updateAllChecked(isChecked: Boolean) {
        updateState {
            copy(ListCart = ListCart.map { it.copy(isSelected = isChecked) })
        }
    }
}