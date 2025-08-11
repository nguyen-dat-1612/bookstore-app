package com.dat.bookstore_app.presentation.features.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.AddToCartUseCase
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.usecases.AddFavoriteUseCase
import com.dat.bookstore_app.domain.usecases.CheckFavoriteUseCase
import com.dat.bookstore_app.domain.usecases.DeleteFavoriteUseCase
import com.dat.bookstore_app.domain.usecases.GetBookByIdUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class DetailBookViewModel @Inject constructor(
    private val getBookById: GetBookByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    private val checkFavoriteUseCase: CheckFavoriteUseCase,
    savedStateHandle: SavedStateHandle
): BaseViewModel<DetailUiState>() {
    override fun initState() =  DetailUiState();

    val book: Book = savedStateHandle.get<Book>("book")!!

    init {
        loadBookDetail(book.id)
    }

    private fun loadBookDetail(bookId: Long) {
        updateState { copy(isLoadBook = true) }
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                when (val bookResult = getBookById(bookId)) {
                    is Result.Success -> updateState { copy(book = bookResult.data, isLoadBook = false) }
                    is Result.Error -> dispatchStateError(bookResult.throwable!!)
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

    fun loadFavoriteState(bookId: Long) {
        viewModelScope.launch(exceptionHandler) {
            when (val favResult = checkFavoriteUseCase(bookId)) {
                is Result.Success -> {
                    val favoriteData = favResult.data
                    updateState {
                        copy(
                            isFavorite = favoriteData != null,
                            favorite = favoriteData
                        )
                    }
                }
                is Result.Error -> {
                    // Không dispatchStateError để tránh hiển thị lỗi không cần thiết
                    updateState { copy(isFavorite = false, favorite = null) }
                }
            }
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

    fun addToFavorite() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                when (val result = addFavoriteUseCase(book.id)) {
                    is Result.Success -> {
                        updateState {
                            copy(
                                addFavoriteSuccess = true,
                                isFavorite = true,
                                favorite = result.data
                            )
                        }
                    }
                    is Result.Error -> dispatchStateError(result.throwable!!)
                }
            } finally {
                updateState {
                    copy(
                        addFavoriteSuccess = false
                    )
                }
                dispatchStateLoading(false)
            }
        }
    }

    fun deleteFavorite() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)

            val favoriteId = uiState.value.favorite?.id
            if (favoriteId == null) {
                dispatchStateError(Exception("Favorite ID is null"))
                dispatchStateLoading(false)
                return@launch
            }

            when (val result = deleteFavoriteUseCase(favoriteId)) {
                is Result.Success -> {
                    updateState {
                        copy(
                            isFavorite = false,
                            favorite = null
                        )
                    }
                }
                is Result.Error -> {
                    dispatchStateError(result.throwable!!)
                }
            }

            dispatchStateLoading(false)
        }
    }
}