package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.data.datasource.remote.dto.AddressRequestDTO
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.network.Result

interface AddressRepository {
    suspend fun getAddresses(): Result<List<Address>>

    suspend fun createAddress(address: AddressRequestDTO): Result<Address>

    suspend fun updateAddress(id: Long, address: AddressRequestDTO): Result<Address>

    suspend fun deleteAddress(id: Long): Result<Any?>

    suspend fun setDefaultAddress(id: Long): Result<Address>

}