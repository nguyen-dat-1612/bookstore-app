package com.dat.bookstore_app.presentation.features.main

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.GetAccessTokenUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase
): BaseViewModel<MainUiState>() {
    override fun initState() = MainUiState()

    init {
        viewModelScope.launch(exceptionHandler) {
            getAccessTokenUseCase().collect{
                isLoggedIn ->
                    updateState {
                        copy(isLoggedIn = isLoggedIn)
                    }
            }
        }
    }

    fun updateLoggedIn(isLoggedIn: Boolean) {
        updateState {
            copy(isLoggedIn = isLoggedIn)
        }
    }
}