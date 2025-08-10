package com.dat.bookstore_app.presentation.features.booklist

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dat.bookstore_app.data.datasource.remote.dto.SearchBooksRequest
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.usecases.GetPagedBooksUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import com.dat.bookstore_app.presentation.features.popular.BookListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val getPagedBooksUseCase: GetPagedBooksUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<BookListUiState>() {

    override fun initState() = BookListUiState()

    companion object {
        const val ARG_SORT_TYPE = "arg_sort_type"
    }

    // Nhận sort type từ fragment args
    private val sortType: Sort = savedStateHandle[ARG_SORT_TYPE] ?: Sort.SOLD_DESC

    private val _request = MutableStateFlow(
        SearchBooksRequest(
            title = null,
            selectedCategoryIds = emptyList(),
            minPrice = null,
            maxPrice = null,
            sortBy = sortType,
            page = 1,
            pageSize = 10
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookListFlow: Flow<PagingData<Book>> = _request
        .flatMapLatest { request ->
            getPagedBooksUseCase(request, onTotalChanged = { total ->
                updateState { copy(totalItems = total) }
            }).let { result ->
                when (result) {
                    is Result.Success -> result.data
                    is Result.Error -> emptyFlow()
                }
            }
        }
        .cachedIn(viewModelScope)

    fun refreshList() {
        _request.update { it.copy(page = 1) }
    }
}
