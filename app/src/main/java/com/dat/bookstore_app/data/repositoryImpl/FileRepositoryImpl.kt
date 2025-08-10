package com.dat.bookstore_app.data.repositoryImpl

import com.dat.bookstore_app.data.datasource.remote.api.FileApi
import com.dat.bookstore_app.data.datasource.remote.dto.FileResponseDTO
import com.dat.bookstore_app.data.mapper.toDomain
import com.dat.bookstore_app.domain.models.File
import com.dat.bookstore_app.domain.repository.FileRepository
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.dat.bookstore_app.utils.extension.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepositoryImpl @Inject constructor(
    private val fileApi: FileApi
) : FileRepository{

    override suspend fun uploadFile(file: MultipartBody.Part, folder: RequestBody): Result<File> {
        return apiCallResponse {
            fileApi.uploadFile(
                file = file,
                folder = folder
            )
        }.map {
            it.toDomain()
        }
    }


}