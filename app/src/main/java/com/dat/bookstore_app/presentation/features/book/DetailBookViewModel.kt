package com.dat.bookstore_app.presentation.features.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.data.datasource.remote.dto.AddToCartUseCase
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.usecases.GetBookByIdUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class DetailBookViewModel @Inject constructor(
    private val getBookById: GetBookByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    savedStateHandle: SavedStateHandle
): BaseViewModel<DetailUiState>() {
    override fun initState() =  DetailUiState();

    val book: Book = savedStateHandle.get<Book>("book")!!

    init {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            when(val result = getBookById(book.id)) {
                is Result.Success -> {
                    updateState {
                        copy(book = result.data)
                    }
                }
                is Result.Error -> dispatchStateError(result.throwable!!)
            }
            dispatchStateLoading(false)
        }

    }

    fun increase() {
        updateState {
            copy(count = count + 1)
        }
    }

    fun decrease() {
        updateState {
            val newCount = max(1, count - 1)
            copy(count = newCount)
        }
    }

    fun addToCart() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                when(val result = addToCartUseCase(book.id, uiState.value.count)) {
                    is Result.Success -> {
                        updateState {
                            copy(
                                book = result.data.book,
                                addCartSuccess = true
                            )
                        }
                    }
                    is Result.Error -> dispatchStateError(result.throwable!!)
                }
            } finally {
                updateState {
                    copy(
                        addCartSuccess = false
                    )
                }
                dispatchStateLoading(false)
            }
        }
    }


}