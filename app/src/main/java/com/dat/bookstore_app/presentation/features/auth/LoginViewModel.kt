package com.dat.bookstore_app.presentation.features.auth

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.LoginGoogleUseCase
import com.dat.bookstore_app.domain.usecases.LoginUseCase
import com.dat.bookstore_app.domain.usecases.ResendVerifyUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginGoogleUseCase: LoginGoogleUseCase,
    private val resendVerifyUseCase: ResendVerifyUseCase
) : BaseViewModel<LoginUiState> (){

    override fun initState() = LoginUiState()

    fun onLogin() {
        updateState {
            copy(isLoading = true)
        }

        viewModelScope.launch(exceptionHandler){
            dispatchStateLoading(true)
            val result = loginUseCase(uiState.value.email, uiState.value.password)
            when(result){
                is Result.Success -> {
                    updateState {
                        copy(isSuccess = true,
                            isLoading = false)
                    }
                }
                is Result.Error -> {
                    if (result.code != 401) {
                        updateState {
                            copy(isSuccess = false,
                                isLoading = false)
                        }
                    } else {
                        updateState {
                            copy(
                                isSuccess = false,
                                isLoading = false,
                                isVerify = true
                            )
                        }
                    }

                    dispatchStateError(e = result.throwable!!)
                }
            }
            dispatchStateLoading(false)
        }
    }

    fun onEmailChange(email: String) {
        updateState {
            copy(email = email)
        }
    }

    fun onPasswordChange(password: String) {
        updateState {
            copy(password = password)
        }
    }

    fun onLoginGoogle(code: String) {
        viewModelScope.launch(exceptionHandler) {
            val result = loginGoogleUseCase(code = code)
            when(result){
                is Result.Success -> {
                    updateState {
                        copy(isSuccess = true)
                    }
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                    updateState {
                        copy(isSuccess = false)
                    }
                }
            }
            dispatchStateLoading(false)

        }
    }

    fun resendVerify() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = resendVerifyUseCase(email = uiState.value.email)
                when(result){
                    is Result.Success -> {
                        updateState {
                            copy(isResendVerify = true)
                        }
                    }
                    is Result.Error -> {
                        dispatchStateError(e = result.throwable!!)
                    }
                }
            } catch (e: Exception) {
                dispatchStateError(e = e)
            } finally {
                dispatchStateLoading(false)
            }
        }
    }
}