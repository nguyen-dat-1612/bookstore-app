package com.dat.bookstore_app.presentation.features.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.GetAccountUseCase
import com.dat.bookstore_app.domain.usecases.UpdateProfileUseCase
import com.dat.bookstore_app.domain.usecases.UploadFileUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class PersonalProfileViewModel @Inject constructor(
    application: Application,
    private val getAccountUseCase: GetAccountUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val uploadFileUseCase: UploadFileUseCase
) : BaseViewModel<PersonalProfileUiState>() {

    override fun initState() = PersonalProfileUiState();

    init {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            val result = getAccountUseCase()
            when (result) {
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
                val avatar = uiState.value.user?.avatar
                val result = updateProfile(
                    id = uiState.value.user?.id ?: uiState.value.user?.id ?: 0,
                    fullName = fullName,
                    address = address,
                    phone = phone,
                    avatar = avatar
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
                        dispatchStateLoading(false)
                    }

                    is Result.Error -> {
                        dispatchStateError(e = result.throwable!!)
                    }
                }
            } catch (e: Exception) {
                dispatchStateError(e = e)
            } finally {
                updateState {
                    copy(isUpdateProfileSuccess = false)
                }
            }
        }
    }

    fun uploadFile(file: MultipartBody.Part, folder: RequestBody) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = uploadFileUseCase(file, folder)
                when (result) {
                    is Result.Success -> {
                        updateState {
                            copy(
                                user = user?.copy(
                                    avatar = result.data.url
                                )
                            )
                        }
                    }
                    is Result.Error -> {
                        dispatchStateError(result.throwable!!)
                    }
                }
            } catch (e: Exception) {
                dispatchStateError(e)
            } finally {
                dispatchStateLoading(false)
            }
        }
    }
}
