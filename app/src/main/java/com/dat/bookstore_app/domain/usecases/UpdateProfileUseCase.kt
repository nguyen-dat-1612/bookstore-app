package com.dat.bookstore_app.domain.usecases

import android.util.Log
import com.dat.bookstore_app.data.datasource.remote.dto.UserInfoRequestDTO
import com.dat.bookstore_app.domain.repository.UserRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result

class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        id: Long,
        fullName: String,
        address: String,
        phone: String,
        avatar: String? = null
    ) : Result<Any?> {
        Log.d("UpdateProfileUseCase", "invoke: $id, $fullName, $address, $phone, $avatar")
        if (id <= 0) {
            return Result.Error(message = "Id is required", throwable = Exception("Id is required"))
        }
        if (fullName.isEmpty()) {
            return Result.Error(message = "Full name is required", throwable = Exception("Full name is required"))
        }
        if (address.isEmpty()) {
            return Result.Error(message = "Address is required", throwable = Exception("Address is required"))
        }
        if (phone.isEmpty()) {
            return Result.Error(message = "Phone is required", throwable = Exception("Phone is required"))
        }
        return userRepository.updateProfile(
            request = UserInfoRequestDTO(
                id = id,
                fullName = fullName,
                address = address,
                phone = phone,
                avatar = avatar
            )
        )
    }
}