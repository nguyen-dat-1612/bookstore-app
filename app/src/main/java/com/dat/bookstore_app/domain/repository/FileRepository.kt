package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.data.datasource.remote.dto.FileResponseDTO
import com.dat.bookstore_app.domain.models.File
import com.dat.bookstore_app.network.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface FileRepository {
    suspend fun uploadFile(file: MultipartBody.Part, folder: RequestBody): Result<File>
}