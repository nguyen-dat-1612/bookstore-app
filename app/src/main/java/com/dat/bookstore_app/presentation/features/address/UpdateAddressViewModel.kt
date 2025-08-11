package com.dat.bookstore_app.presentation.features.address

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.enums.AddressType
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.models.Commune
import com.dat.bookstore_app.domain.models.Province
import com.dat.bookstore_app.domain.usecases.DeleteAddressUseCase
import com.dat.bookstore_app.domain.usecases.GetCommunesByProvinceUseCase
import com.dat.bookstore_app.domain.usecases.GetProvincesUseCase
import com.dat.bookstore_app.domain.usecases.SetDefaultAddressUseCase
import com.dat.bookstore_app.domain.usecases.UpdateAddressUseCase
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

@HiltViewModel
class UpdateAddressViewModel @Inject constructor(
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
    private val getProvincesUseCase: GetProvincesUseCase,
    private val getCommunesByProvinceUseCase: GetCommunesByProvinceUseCase,
    private val setDefaultAddressUseCase: SetDefaultAddressUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<UpdateAddressUiState>() {

    override fun initState() = UpdateAddressUiState()

    val address = savedStateHandle.get<Address>("address")

    fun loadProvinces() {
        viewModelScope.launch(exceptionHandler) {
            try {
                val provinces = getProvincesUseCase()
                updateState { copy(provinces = provinces) }
            } catch (e: Exception) {
                dispatchStateError(e)
            }
        }
    }

    fun loadCommunesByProvince(provinceId: String) {
        viewModelScope.launch(exceptionHandler) {
            try {
                val communes = getCommunesByProvinceUseCase(provinceId)
                updateState { copy(communes = communes) }
            } catch (e: Exception) {
                dispatchStateError(e)
            }
        }
    }

    fun chooseProvince(province: Province) {
        updateState { copy(chooseProvince = province) }
    }

    fun chooseCommune(commune: Commune) {
        updateState { copy(chooseCommune = commune) }
    }

    fun updateAddressType(addressType: AddressType) {
        updateState { copy(addressType = addressType) }
    }

    fun updateAddress(
        fullName: String,
        phoneNumber: String,
        addressDetail: String,
        isDefault: Boolean
    ) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                Log.d("UpdateAddressViewModel", "updateAddress: $isDefault")
                val result = updateAddressUseCase(
                    id = address?.id!!,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    addressDetail = addressDetail,
                    addressType = uiState.value.addressType!!,
                    province = uiState.value.chooseProvince?.name!!,
                    ward = uiState.value.chooseCommune?.name!!,
                    isDefault = isDefault
                )

                when (result) {
                    is Result.Success -> {
                        // Nếu địa chỉ này là default, gọi thêm API setDefaultAddress
                        if (isDefault) {
                            val defaultResult = setDefaultAddressUseCase(address.id)
                            if (defaultResult is Result.Error) {
                                dispatchStateError(defaultResult.throwable!!)
                                return@launch
                            }
                        }

                        updateState {
                            copy(
                                address = result.data,
                                isUpdateSuccess = true
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



    fun deleteAddress() {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = deleteAddressUseCase(address?.id!!)
                when (result) {
                    is Result.Success -> {
                        updateState {
                            copy(
                                isDeleteSuccess = true
                            )
                        }
                        dispatchStateLoading(false)
                    }

                    is Result.Error -> {
                        dispatchStateError(result.throwable!!)
                        dispatchStateLoading(false)
                    }
                }
            } catch (e: Exception) {
                dispatchStateError(e)
            }
        }

    }
}
