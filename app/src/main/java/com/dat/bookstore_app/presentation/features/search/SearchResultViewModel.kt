package com.dat.bookstore_app.presentation.features.search

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.dat.bookstore_app.data.datasource.remote.dto.SearchBooksRequest
import com.dat.bookstore_app.data.mapper.toUiModel
import com.dat.bookstore_app.domain.enums.Sort
import com.dat.bookstore_app.domain.models.CategoryUiModel
import com.dat.bookstore_app.domain.usecases.GetBooksTotalOnlyUseCase
import com.dat.bookstore_app.domain.usecases.GetCategoryUseCase
import com.dat.bookstore_app.domain.usecases.GetPagedBooksUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val getPagedBooksUseCase: GetPagedBooksUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val getBooksTotalOnlyUseCase: GetBooksTotalOnlyUseCase
) : BaseViewModel<SearchResultUiState>() {

    override fun initState() = SearchResultUiState()

    var query: String = ""
    
    private val _previewRequest = MutableStateFlow<SearchBooksRequest?>(null) // để gọi thử
    private val _actualRequest = MutableStateFlow<SearchBooksRequest?>(null) // khi apply mới dùng

    val previewTotal = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val booksFlow = _actualRequest
        .filterNotNull()
        .flatMapLatest { request ->
            dispatchStateLoading(true)
            try {
                when (val result = getPagedBooksUseCase(request, onTotalChanged = { total ->
                    updateState { copy(total = total) }
                })) {
                    is Result.Success -> result.data
                    is Result.Error -> {
                        dispatchStateError(result.throwable!!)
                        emptyFlow()
                    }
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
        .cachedIn(viewModelScope)


    init {
        viewModelScope.launch(exceptionHandler) {
            when (val result = getCategoryUseCase()) {
                is Result.Success -> updateState {
                    copy(allCategories = result.data.map {
                        it.toUiModel()
                    })
                }
                is Result.Error -> dispatchStateError(result.throwable!!)
            }
        }
    }

    fun onSearchTriggered() {
        val request = buildSearchRequestFromUiState()
        _actualRequest.value = request
    }

    private fun buildSearchRequestFromUiState(): SearchBooksRequest {

        return SearchBooksRequest(
            title = query,
            selectedCategoryIds = uiState.value.allCategories.filter { it.isSelected }.map { it.id },
            minPrice = uiState.value.minPrice,
            maxPrice = uiState.value.maxPrice,
            sortBy = uiState.value.sortBy,
            page = 1,
            pageSize = 10
        )
    }

    fun changeSort(sort: Sort) {
        updateState {
            copy(sortBy = sort)
        }
        _actualRequest.value = _actualRequest.value?.copy(sortBy = sort)
    }

    fun previewFilters(
        allCategories: List<CategoryUiModel>,
        minPrice: Int?,
        maxPrice: Int?
    ) {
        val selectedIds = allCategories.filter { it.isSelected }.map { it.id }

        val previewRequest = SearchBooksRequest(
            title = query,
            selectedCategoryIds = selectedIds,
            minPrice = minPrice,
            maxPrice = maxPrice,
            sortBy = uiState.value.sortBy,
            page = 1,
            pageSize = 1
        )

        _previewRequest.value = previewRequest

        viewModelScope.launch(exceptionHandler) {
            when (val result = getBooksTotalOnlyUseCase(previewRequest)) {
                is Result.Success -> previewTotal.value = result.data
                is Result.Error -> dispatchStateError(result.throwable!!)
            }
        }
    }

    fun applyFilters() {
        val appliedRequest = _previewRequest.value ?: buildSearchRequestFromUiState()

        updateState {
            copy(
                allCategories = appliedRequest.selectedCategoryIds
                    .let { selectedIds ->
                        allCategories.map { it.copy(isSelected = selectedIds.contains(it.id)) }
                    },
                minPrice = appliedRequest.minPrice,
                maxPrice = appliedRequest.maxPrice
            )
        }

        _actualRequest.value = appliedRequest
    }

    fun resetFilters() {
        val resetList = uiState.value.allCategories.map { it.copy(isSelected = false) }

        updateState {
            copy(
                allCategories = resetList,
                minPrice = null,
                maxPrice = null
            )
        }

        _actualRequest.value = SearchBooksRequest( // <- tạo mới
            title = query,
            selectedCategoryIds = emptyList(),
            minPrice = null,
            maxPrice = null,
            sortBy = uiState.value.sortBy,
            page = 1,
            pageSize = 10
        )
    }

    fun clearData() {
        query = ""
        _actualRequest.value = null

        resetFilters()
    }
}
