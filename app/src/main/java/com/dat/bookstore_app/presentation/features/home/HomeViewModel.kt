package com.dat.bookstore_app.presentation.features.home

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.HomeUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCase: HomeUseCase,
): BaseViewModel<HomeUiState>() {
    override fun initState() = HomeUiState()

    init {
        dispatchStateLoading(true)
        viewModelScope.launch (exceptionHandler){
            val response = homeUseCase.loadHomeBooks()
            when(response) {
                is Result.Success -> {
                    updateState {
                        copy(homeBooksResult = response.data)
                    }
                }
                is Result.Error -> {
                    dispatchStateError(response.throwable!!)
                }
            }
        }
        dispatchStateLoading(false)
    }
}