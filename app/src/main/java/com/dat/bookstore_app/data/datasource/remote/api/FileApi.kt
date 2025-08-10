package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.FileResponseDTO
import com.dat.bookstore_app.network.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {
    @Multipart
    @POST("files")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("folder") folder: RequestBody
    ): ApiResponse<FileResponseDTO>
}