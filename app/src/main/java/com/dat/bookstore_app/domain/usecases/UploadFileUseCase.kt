package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.models.File
import com.dat.bookstore_app.domain.repository.FileRepository
import okhttp3.MultipartBody
import javax.inject.Inject
import com.dat.bookstore_app.network.Result
import okhttp3.RequestBody

class UploadFileUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(file: MultipartBody.Part, folder: RequestBody): Result<File> {

        return fileRepository.uploadFile(file, folder)
    }
}