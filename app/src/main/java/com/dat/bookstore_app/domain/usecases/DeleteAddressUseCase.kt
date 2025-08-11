package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.repository.AddressRepository
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
class DeleteAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke(id: Long): Result<Any?> {
        if (id <= 0) {
            return Result.Error(
                message = "Invalid address ID",
                throwable = IllegalArgumentException("Invalid address ID")
            )
        }
        return addressRepository.deleteAddress(id)
    }

}