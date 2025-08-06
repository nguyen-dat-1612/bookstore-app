package com.dat.bookstore_app.presentation.features.favorites

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dat.bookstore_app.data.datasource.remote.dto.GetFavoriteRequest
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.models.Favorite
import com.dat.bookstore_app.domain.usecases.AddToCartUseCase
import com.dat.bookstore_app.domain.usecases.DeleteFavoriteUseCase
import com.dat.bookstore_app.domain.usecases.GetPagedFavoritesUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    private val getFavoriteListUseCase: GetPagedFavoritesUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : BaseViewModel<FavoriteListUiState>() {

    override fun initState() = FavoriteListUiState()

    val favoritePagingFlow = MutableStateFlow<PagingData<Favorite>>(PagingData.empty())

    fun loadFavorite(
        sort: Sort
    ) {
        val request = GetFavoriteRequest(
            page = 1,
            size = 10,
            sort = sort
        )
        when(val result = getFavoriteListUseCase(request) { total ->
            updateState { copy( total = total) }
        }) {
            is Result.Success -> {
                viewModelScope.launch(exceptionHandler) {
                    result.data
                        .cachedIn(viewModelScope)
                        .collectLatest {
                            favoritePagingFlow.value = it
                        }
                }
            }
            is Result.Error -> {
                dispatchStateError(e = result.throwable!!)
            }
        }
    }

    fun deleteFavorite(bookId: Long, sort: Sort) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = deleteFavoriteUseCase(bookId)
            when (result) {
                is Result.Success -> {
                    loadFavorite(sort)
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                }
            }
            dispatchStateLoading(false)
        }
    }

    fun addToCart(bookId: Long) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = addToCartUseCase(bookId, quantity = 1)
                when (result) {
                    is Result.Success -> {
                        updateState {
                            copy(addCartSuccess = true)
                        }
                    }
                    is Result.Error -> {
                        dispatchStateError(e = result.throwable!!)
                    }
                }
            } finally {
                updateState {
                    copy(addCartSuccess = false)
                }
                dispatchStateLoading(false)
            }
        }
    }

}