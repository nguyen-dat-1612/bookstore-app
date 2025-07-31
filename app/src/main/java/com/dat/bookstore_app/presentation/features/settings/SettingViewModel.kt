package com.dat.bookstore_app.presentation.features.settings

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.LogoutUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<SettingUiState>() {
    override fun initState() = SettingUiState()

    fun logout() {
        viewModelScope.launch {
            dispatchStateLoading(true)
            val result = logoutUseCase()
            when(result) {
                is Result.Success -> {
                    updateState {
                        copy(LogoutSuccess = true)
                    }
                }
                is Result.Error -> {
                    updateState {
                        copy(LogoutSuccess = true)
                    }
                    dispatchStateError(result.throwable!!)
                }

            }
            dispatchStateLoading(false)
        }
    }

}