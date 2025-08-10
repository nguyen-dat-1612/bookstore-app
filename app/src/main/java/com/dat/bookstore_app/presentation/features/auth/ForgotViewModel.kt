package com.dat.bookstore_app.presentation.features.auth

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotViewModel @Inject constructor(

): BaseViewModel<ForgotUiState>(){
    override fun initState() = ForgotUiState()

    fun onForgot() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)

        }
    }
}