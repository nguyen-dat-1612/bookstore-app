package com.dat.bookstore_app.presentation.features.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.data.datasource.remote.dto.SearchBooksRequest
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.usecases.GetBooksUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

@HiltViewModel
class BookPagerViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    savedStateHandle: SavedStateHandle
): BaseViewModel<BookPagerUiState>() {

    override fun initState() = BookPagerUiState()

    // Nhận sort type từ fragment args
    private val sortType: Sort = savedStateHandle[ARG_SORT_TYPE] ?: Sort.SOLD_DESC

    init {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val request = SearchBooksRequest(
                    selectedCategoryIds = emptyList(),
                    minPrice = null,
                    maxPrice = null,
                    sortBy = sortType,
                    page = 1,
                    pageSize = 10
                )
                val result = getBooksUseCase(request)
                when(result) {
                    is Result.Success -> {
                        updateState {
                            copy(bookList = result.data.books)
                        }
                    }
                    is Result.Error -> {
                        dispatchStateError(result.throwable!!)
                    }
                }
            } catch (e: Exception) {
                dispatchStateError(e)
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

    companion object {
        const val ARG_SORT_TYPE = "arg_sort_type"
    }

}