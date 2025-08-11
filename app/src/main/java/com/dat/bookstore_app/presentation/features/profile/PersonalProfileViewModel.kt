package com.dat.bookstore_app.presentation.features.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.usecases.GetAccountUseCase
import com.dat.bookstore_app.domain.usecases.GetAddressUseCase
import com.dat.bookstore_app.domain.usecases.UpdateProfileUseCase
import com.dat.bookstore_app.domain.usecases.UploadFileUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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
    private val uploadFileUseCase: UploadFileUseCase,
    private val getAddressUseCase: GetAddressUseCase
) : BaseViewModel<PersonalProfileUiState>() {

    override fun initState() = PersonalProfileUiState();

    init {
        loadProfileAndAddress()
    }

    fun loadProfileAndAddress() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)

            supervisorScope {
                val accountDeferred = async { getAccountUseCase() }
                val addressDeferred = async { getAddressUseCase() }

                val accountResult = accountDeferred.await()
                val addressResult = addressDeferred.await()

                when (accountResult) {
                    is Result.Success -> updateState { copy(user = accountResult.data) }
                    is Result.Error -> dispatchStateError(accountResult.throwable!!)
                }

                when (addressResult) {
                    is Result.Success -> updateState { copy(addressList = addressResult.data) }
                    is Result.Error -> dispatchStateError(addressResult.throwable!!)
                }
            }

            dispatchStateLoading(false)
        }
    }


    fun updateAccount(fullName: String, address: String, phone: String) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            updateState {
                copy(
                    isLoadingUpdateProfile = true
                )
            }
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
                                isUpdateProfileSuccess = true,
                                isLoadingUpdateProfile = false
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
        updateState {
            copy(
                isLoadingUpdateProfile = true
            )
        }
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
                                ),
                                isLoadingUpdateProfile = false
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
