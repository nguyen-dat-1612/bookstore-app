package com.dat.bookstore_app.presentation.features.auth

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.ResetPasswordUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : BaseViewModel<ResetPasswordUiState>() {

    override fun initState() = ResetPasswordUiState()

    fun resetPassword(
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            updateState {
                copy(isLoading = true)
            }
            try {
                val result = resetPasswordUseCase(uiState.value.token!!,newPassword, confirmPassword)
                when (result) {
                    is Result.Success -> {
                        updateState {
                            copy(isLoading = false, isSuccess = true)
                        }
                    }
                    is Result.Error -> {
                        updateState {
                            copy(isLoading = false, isSuccess = false)
                        }
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

    fun setToken(
        token: String
    ) {
        updateState {
            copy(
                token = token
            )
        }
    }
}