package com.dat.bookstore_app.presentation.features.profile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.GetAccountUseCase
import com.dat.bookstore_app.domain.usecases.UpdateTokenUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val updateTokenUseCase: UpdateTokenUseCase,
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

    fun fetchAndEnableNotification() {
        if (uiState.value.isNotificationEnabled) return

        val userId = uiState.value.user?.id ?: return

        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("SettingViewModel", "Token: $token")

                when (val result = updateTokenUseCase(userId, token)) {
                    is Result.Success -> {
                        updateState { copy(isNotificationEnabled = true, currentToken = token) }
                    }
                    is Result.Error -> {
                        updateState { copy(isNotificationEnabled = false) }
                        result.throwable?.let { dispatchStateError(it) }
                    }
                }
            } catch (e: Exception) {
                updateState { copy(isNotificationEnabled = false) }
                dispatchStateError(e)
            } finally {
                dispatchStateLoading(false)
            }
        }
    }
}