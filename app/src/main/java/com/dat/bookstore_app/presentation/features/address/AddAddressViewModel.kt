package com.dat.bookstore_app.presentation.features.address

import androidx.lifecycle.viewModelScope
import com.dat.bookstore_app.domain.enums.AddressType
import com.dat.bookstore_app.domain.models.Commune
import com.dat.bookstore_app.domain.models.Province
import com.dat.bookstore_app.domain.repository.AddressRepository
import com.dat.bookstore_app.domain.usecases.AddAddressUseCase
import com.dat.bookstore_app.domain.usecases.GetCommunesByProvinceUseCase
import com.dat.bookstore_app.domain.usecases.GetProvincesUseCase
import com.dat.bookstore_app.domain.usecases.SetDefaultAddressUseCase
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.presentation.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val addAddressUseCase: AddAddressUseCase,
    private val getProvincesUseCase: GetProvincesUseCase,
    private val getCommunesByProvinceUseCase: GetCommunesByProvinceUseCase,
    private val setDefaultAddressUseCase: SetDefaultAddressUseCase
) : BaseViewModel<AddAddressUiState>() {

    override fun initState() = AddAddressUiState()

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

    fun addAddress(
        fullName: String,
        phoneNumber: String,
        addressDetail: String,
        isDefault: Boolean
    ) {
        viewModelScope.launch(exceptionHandler) {
            dispatchStateLoading(true)
            try {
                val result = addAddressUseCase(
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
                        // Nếu là default thì gọi thêm API setDefaultAddress
                        if (isDefault) {
                            val defaultResult = setDefaultAddressUseCase(result.data.id!!)
                            if (defaultResult is Result.Error) {
                                dispatchStateError(defaultResult.throwable!!)
                                return@launch
                            }
                        }

                        updateState {
                            copy(
                                address = result.data,
                                isSuccess = true
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
