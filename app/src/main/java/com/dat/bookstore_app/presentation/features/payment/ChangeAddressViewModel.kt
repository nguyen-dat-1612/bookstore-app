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

    fun loadAddressFromArgsIfAvailable() {
        val fullNameLive = savedStateHandle.getLiveData<String>("fullName")
        val phoneLive = savedStateHandle.getLiveData<String>("phone")
        val addressLive = savedStateHandle.getLiveData<String>("address")

        // Combine cả 3 lại
        viewModelScope.launch {
            val fullName = fullNameLive.value
            val phone = phoneLive.value
            val address = addressLive.value
            if (fullName != null && phone != null && address != null) {
                updateState {
                    copy(
                        fullName = fullName,
                        phone = phone,
                        address = address
                    )
                }
            }
        }
    }

    fun updateAddress(fullName: String, phone: String, address: String) {
        viewModelScope.launch {
            updateState {
                copy(
                    fullName = fullName,
                    phone = phone,
                    address = address
                )
            }
        }
    }
}