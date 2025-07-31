package com.dat.bookstore_app.presentation.features.register

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.RegisterUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : BaseViewModel<RegisterUiState>(){
    override fun initState() = RegisterUiState()

    fun register(fullName: String, email: String, phone: String, password: String) {
        viewModelScope.launch (exceptionHandler){
            dispatchStateLoading(true)
            val result = registerUseCase(fullName, email, phone, password)
            when(result) {
                is Result.Success -> {
                    updateState { copy(isSuccess = true) }
                }
                is Result.Error -> {
                    updateState { copy(isSuccess = false) }
                    dispatchStateError(result.throwable!!)
                }
            }
            dispatchStateLoading(false)

        }
    }
}