package com.plus.baseandroidapp.presentation.base

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<S : Any> : ViewModel() {

    abstract fun initState(): S

    private val _uiState = MutableStateFlow(initState())
    val uiState: StateFlow<S> = _uiState

    val currentState: S get() = _uiState.value

    val errorsState = ErrorsState()
    val loadingState = LoadingState()

    protected val exceptionHandler = CoroutineExceptionHandler { _, e ->
        dispatchStateError(e)
    }

    protected fun updateState(update: S.() -> S) {
        _uiState.update { it.update() }
    }

    protected fun dispatchStateError(e: Throwable) {
        errorsState.emitError(e)
        updateState { this }
    }

    protected fun dispatchStateLoading(isLoading: Boolean) {
        loadingState.updateLoadingState(isLoading)
    }
}
