package com.dat.bookstore_app.presentation.features.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.ClearSearchUseCase
import com.dat.bookstore_app.domain.usecases.GetSearchUseCase
import com.dat.bookstore_app.domain.usecases.SaveSearchUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchInputViewModel @Inject constructor(
    private val getSearchUseCase: GetSearchUseCase,
    private val saveSearchUseCase: SaveSearchUseCase,
    private val clearSearchUseCase: ClearSearchUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SearchInputUiState>() {
    override fun initState() = SearchInputUiState()

    val query = savedStateHandle.get<String>("query") ?: ""

    init {
        viewModelScope.launch (exceptionHandler){
            dispatchStateLoading(true)
            getSearchUseCase().collectLatest { result ->
                when(result) {
                    is Result.Success -> {
                        updateState {
                            copy(listHistorySearch = result.data)
                        }
                    }
                    is Result.Error -> {
                        dispatchStateError(e = result.throwable!!)
                    }
                }
            }
            dispatchStateLoading(false)
        }
    }

    fun saveQuery(query: String) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = saveSearchUseCase(query)
            when(result) {
                is Result.Success -> {
                    dispatchStateLoading(false)
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                    dispatchStateLoading(false)
                }
            }
        }
    }

    fun clearSearch() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = clearSearchUseCase()
            when(result) {
                is Result.Success -> {
                    dispatchStateLoading(false)
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                    dispatchStateLoading(false)
                }
            }
        }
    }
}