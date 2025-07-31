package com.dat.bookstore_app.presentation.common.base

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class ErrorsState {

    private val _errors = MutableSharedFlow<Throwable>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val errors: SharedFlow<Throwable> = _errors

    fun emitError(throwable: Throwable) {
        _errors.tryEmit(throwable)
    }

    suspend fun emitErrorSuspend(throwable: Throwable) {
        _errors.emit(throwable)
    }
}