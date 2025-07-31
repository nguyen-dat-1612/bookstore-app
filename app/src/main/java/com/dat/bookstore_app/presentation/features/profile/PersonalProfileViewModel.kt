package com.dat.bookstore_app.presentation.features.profile

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.GetAccountUseCase
import com.dat.bookstore_app.domain.usecases.UpdateProfile
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalProfileViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val updateProfile: UpdateProfile
) : BaseViewModel<PersonalProfileUiState>(){

    override fun initState() = PersonalProfileUiState();

    init {
        viewModelScope.launch (exceptionHandler){
            dispatchStateLoading(true)
            val result = getAccountUseCase()
            when(result) {
                is Result.Success -> {
                    updateState {
                        copy(
                            user = result.data
                        )
                    }
                }
                is Result.Error -> {
                    dispatchStateError(e = result.throwable!!)
                }
            }
            dispatchStateLoading(false)
        }
    }

    fun updateAccount(fullName: String, address: String, phone: String) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = updateProfile(
                    id = uiState.value.user?.id!!,
                    fullName = fullName,
                    address = address,
                    phone = phone,
                    avatar = uiState.value.user?.avatar!!
                )
                when (result) {
                    is Result.Success -> {
                        updateState {
                            copy(
                                user = user?.copy(
                                    fullName = fullName,
                                    address = address,
                                    phone = phone
                                ),
                                isUpdateProfileSuccess = true
                            )
                        }
                    }
                    is Result.Error -> {
                        dispatchStateError(e = result.throwable!!)
                    }
                }
            } finally {
                updateState {
                    copy(isUpdateProfileSuccess = false)
                }
                dispatchStateLoading(false)

            }
        }
    }

}