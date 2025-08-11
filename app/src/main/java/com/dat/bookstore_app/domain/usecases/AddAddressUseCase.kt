package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.data.datasource.remote.dto.AddressRequestDTO
import com.dat.bookstore_app.domain.enums.AddressType
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.repository.AddressRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class AddAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(
        fullName: String,
        phoneNumber: String,
        province: String,
        ward: String,
        addressDetail: String,
        addressType: AddressType?,
        isDefault: Boolean
    ): Result<Address> {



        // 1. Kiểm tra rỗng
        if (fullName.isBlank() || phoneNumber.isBlank() ||
            province.isBlank() || ward.isBlank() || addressDetail.isBlank()
        ) {
            return Result.Error(
                message = "Vui lòng nhập đầy đủ thông tin",
                throwable = IllegalArgumentException("Vui lòng nhập đầy đủ thông tin")
            )
        }

        // 2. Kiểm tra họ tên: ít nhất 2 từ, không chứa số hoặc ký tự đặc biệt
        val nameRegex = Regex("^[\\p{L}]+(?:\\s[\\p{L}]+)+$")
        if (!nameRegex.matches(fullName.trim())) {
            return Result.Error(
                message = "Họ và tên không hợp lệ (ít nhất 2 từ, chỉ chứa chữ cái)",
                throwable = IllegalArgumentException("Họ và tên không hợp lệ")
            )
        }

        // 3. Kiểm tra số điện thoại Việt Nam
        val phoneRegex = Regex("^(0[3|5|7|8|9][0-9]{8}|\\+84[3|5|7|8|9][0-9]{8})$")
        if (!phoneRegex.matches(phoneNumber.trim())) {
            return Result.Error(
                message = "Số điện thoại không hợp lệ",
                throwable = IllegalArgumentException("Số điện thoại không hợp lệ")
            )
        }

        // 4. Kiểm tra loại địa chỉ
        if (addressType == null) {
            return Result.Error(
                message = "Vui lòng chọn loại địa chỉ",
                throwable = IllegalArgumentException("Loại địa chỉ trống")
            )
        }

        val request = AddressRequestDTO(
            fullName = fullName.trim(),
            phoneNumber = phoneNumber.trim(),
            province = province.trim(),
            ward = ward.trim(),
            addressDetail = addressDetail.trim(),
            addressType = addressType,
            isDefault = isDefault
        )

        // 5. Gọi repository để tạo địa chỉ
        return addressRepository.createAddress(request)
    }
}
