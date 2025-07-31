package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.FileResponseDTO
import com.dat.bookstore_app.network.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {
    @POST("files")
    suspend fun uploadFile(
        @Part ("file") file: MultipartBody.Part,
        @Part("folder") folder: String
    ): ApiResponse<FileResponseDTO>
}