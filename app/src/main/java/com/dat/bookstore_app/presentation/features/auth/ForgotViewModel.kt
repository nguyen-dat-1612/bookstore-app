package com.dat.bookstore_app.presentation.features.auth

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.ForgotPasswordUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
): BaseViewModel<ForgotUiState>(){
    override fun initState() = ForgotUiState()

    fun onForgot(email: String) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            updateState {
                copy(isLoading = true)
            }
            try {
                val result = forgotPasswordUseCase(email = email)
                when(result) {
                    is Result.Success -> {
                        updateState {
                            copy(
                                isSuccess = true,
                                isLoading = false
                            )
                        }
                    }
                    is Result.Error -> {
                        dispatchStateError(e = result.throwable!!)
                    }
                }
            } catch (e: Exception) {
                dispatchStateError(e)
            } finally {
                dispatchStateLoading(false)
            }
        }
    }
}