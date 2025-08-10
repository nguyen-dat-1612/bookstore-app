package com.dat.bookstore_app.presentation.features.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeAddressViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<ChangeAddressUiState>(){

    override fun initState() = ChangeAddressUiState()

    init {
        loadAddressFromArgsIfAvailable()
    }

    private fun loadAddressFromArgsIfAvailable() {
        fun String?.orNullIfPlaceholder(placeholders: List<String>) =
            if (this == null || this in placeholders) null else this

        updateState {
            copy(
                fullName = savedStateHandle.get<String>("fullName")
                    .orNullIfPlaceholder(listOf("Chưa có tên")),
                phone = savedStateHandle.get<String>("phone")
                    .orNullIfPlaceholder(listOf("Chưa có số")),
                address = savedStateHandle.get<String>("address")
                    .orNullIfPlaceholder(listOf("Chưa có địa chỉ"))
            )
        }
    }
}