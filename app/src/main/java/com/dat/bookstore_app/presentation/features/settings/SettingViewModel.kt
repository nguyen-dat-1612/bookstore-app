package com.dat.bookstore_app.presentation.features.settings

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.LogoutUseCase
import com.dat.bookstore_app.domain.usecases.RemoveTokenUseCase
import com.dat.bookstore_app.domain.usecases.UpdateTokenUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import com.dat.bookstore_app.network.Result
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val updateTokenUseCase: UpdateTokenUseCase,
    private val removeTokenUseCase: RemoveTokenUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<SettingUiState>() {

    override fun initState() = SettingUiState()

    val userId = savedStateHandle.get<Long>("userId")!!

    fun logout() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = logoutUseCase()
            when (result) {
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

    fun fetchAndEnableNotification() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("SettingViewModel", "Token: $token")
                val result = updateTokenUseCase(userId, token)

                when (result) {
                    is Result.Success -> {
                        updateState {
                            copy(isNotificationEnabled = true, currentToken = token)
                        }
                    }

                    is Result.Error -> {
                        updateState {
                            copy(isNotificationEnabled = false)
                        }
                        dispatchStateError(result.throwable!!)
                    }
                }
            } catch (e: Exception) {
                updateState {
                    copy(isNotificationEnabled = false)
                }
                dispatchStateError(e)
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

    fun disableNotification() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("SettingViewModel", "Token: $token")
                val result = removeTokenUseCase(userId, token)
                when (result) {
                    is Result.Success -> {
                        updateState {
                            copy(isNotificationEnabled = false, currentToken = null)
                        }
                    }

                    is Result.Error -> {
                        dispatchStateError(result.throwable!!)
                    }
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
    }

    fun updateToken(token: String) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = updateTokenUseCase(userId, token)
                when (result) {
                    is Result.Success -> {
                        updateState {
                            copy(isChangeSuccess = true)
                        }
                    }

                    is Result.Error -> {
                        updateState {
                            copy(isChangeSuccess = false)
                        }
                        dispatchStateError(result.throwable!!)
                    }
                }
            } finally {
                dispatchStateLoading(false)
            }
        }
    }
}
