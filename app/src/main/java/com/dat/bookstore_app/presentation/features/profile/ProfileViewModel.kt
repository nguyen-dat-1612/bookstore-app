package com.dat.bookstore_app.presentation.features.profile

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.GetAccountUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase
) : BaseViewModel<ProfileUiState>(){
    override fun initState() = ProfileUiState()

    init {
        viewModelScope.launch (exceptionHandler) {
            dispatchStateLoading(true)
            when (val result = getAccountUseCase()) {
                is Result.Success -> {
                    updateState {
                        copy(user = result.data)
                    }
                }
                is Result.Error -> {
                    dispatchStateError(result.throwable!!)
                }
            }
            dispatchStateLoading(false)
        }
    }
}