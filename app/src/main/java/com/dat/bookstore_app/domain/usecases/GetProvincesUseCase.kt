package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.models.Province
import com.dat.bookstore_app.domain.repository.ProvinceRepository
import javax.inject.Inject

class GetProvincesUseCase @Inject constructor(
    private val repo: ProvinceRepository
) {
    suspend operator fun invoke(): List<Province> {
        return repo.getProvinces()
    }
}
