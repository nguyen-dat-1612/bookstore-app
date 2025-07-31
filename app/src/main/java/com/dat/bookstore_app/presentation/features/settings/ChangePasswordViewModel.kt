package com.dat.bookstore_app.presentation.features.settings

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.ChangePasswordUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : BaseViewModel<ChangePasswordUiState>() {

    override fun initState() =  ChangePasswordUiState()

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            when(val result = changePasswordUseCase(currentPassword, newPassword)) {
                is Result.Success -> updateState {
                    copy(
                        isChangePasswordSuccess = true,
                        messageError = null
                    )
                }
                is Result.Error ->  {
                    updateState {
                        copy(
                            isChangePasswordSuccess = false,
                            messageError = result.message
                        )
                    }
                    dispatchStateError(e = result.throwable!!)
                }
            }
            dispatchStateLoading(false)

        }
    }
}