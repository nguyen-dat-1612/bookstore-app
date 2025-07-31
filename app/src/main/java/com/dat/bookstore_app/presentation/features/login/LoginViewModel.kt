package com.dat.bookstore_app.presentation.features.login

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.LoginUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginUiState> (){

    override fun initState() = LoginUiState()

    fun onLogin() {
        viewModelScope.launch(exceptionHandler){
            dispatchStateLoading(true)
            when(loginUseCase(uiState.value.email, uiState.value.password)){
                is Result.Success -> {
                    updateState {
                        copy(isSuccess = true)
                    }
                }
                is Result.Error -> {
                    updateState {
                        copy(isSuccess = false)
                    }
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


}