package com.dat.bookstore_app.presentation.features.notification

import com.dat.bookstore_app.domain.usecases.GetNotificationUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

//@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationUseCase: GetNotificationUseCase
) : BaseViewModel<NotificationUiState>() {
    override fun initState() =  NotificationUiState()

    private val _uiState = MutableStateFlow(0)



}