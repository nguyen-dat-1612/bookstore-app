package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.repository.AddressRepository
import com.dat.bookstore_app.network.Result
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository
) {
    suspend operator fun invoke() : Result<List<Address>> {
        return addressRepository.getAddresses()
    }

}