package com.dat.bookstore_app.presentation.features.settings

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.CreatePasswordUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class CreatePasswordViewModel @Inject constructor(
    private val createPasswordUseCase: CreatePasswordUseCase
) : BaseViewModel<CreatePasswordUiState>(){
    override fun initState() = CreatePasswordUiState()

    fun createPassword(password: String, confirmPassword: String) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = createPasswordUseCase(password, confirmPassword)
                when(result) {
                    is Result.Success -> {
                        dispatchStateLoading(false)
                        updateState {
                            copy(
                                isCreatePasswordSuccess = true
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