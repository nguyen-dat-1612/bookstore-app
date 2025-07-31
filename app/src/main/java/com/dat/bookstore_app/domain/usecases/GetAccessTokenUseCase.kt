package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.di.qualifier.IODispatcher
import com.dat.bookstore_app.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
){
    operator fun invoke(): Flow<Boolean> {
        return userRepository.getToken()
            .map { !it.isNullOrEmpty() }
            .flowOn(ioDispatcher)
    }

}