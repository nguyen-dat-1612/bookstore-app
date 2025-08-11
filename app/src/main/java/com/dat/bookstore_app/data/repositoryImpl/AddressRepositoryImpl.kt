package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.remote.api.AddressApi
import com.dat.bookstore_app.data.datasource.remote.dto.AddressRequestDTO
import com.dat.bookstore_app.data.mapper.toAddressDomain
import com.dat.bookstore_app.data.mapper.toDomain
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.domain.repository.AddressRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.dat.bookstore_app.utils.extension.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val api: AddressApi
): AddressRepository {
    override suspend fun getAddresses(): Result<List<Address>> {
        return apiCallResponse {
            api.getAddresses()
        }.map {
            it.toAddressDomain()
        }
    }

    override suspend fun createAddress(address: AddressRequestDTO): Result<Address> {
        return apiCallResponse {
            api.createAddress(address)
        }.map {
            it.toDomain()
        }
    }

    override suspend fun updateAddress(id: Long, address: AddressRequestDTO): Result<Address> {
        address
        return apiCallResponse {
            api.updateAddress(id, address)
        }.map {
            it.toDomain()
        }
    }

    override suspend fun deleteAddress(id: Long): Result<Any?> {
        return apiCallResponse {
            api.deleteAddress(id)
        }
    }

    override suspend fun setDefaultAddress(id: Long): Result<Address> {
        return apiCallResponse {
            api.setDefaultAddress(id)
        }.map {
            it.toDomain()
        }
    }

}