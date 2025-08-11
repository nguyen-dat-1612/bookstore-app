package com.dat.bookstore_app.domain.usecases

import com.dat.bookstore_app.domain.models.Commune
import com.dat.bookstore_app.domain.repository.ProvinceRepository
import javax.inject.Inject

class GetCommunesByProvinceUseCase @Inject constructor(
    private val repo: ProvinceRepository
) {
    suspend operator fun invoke(idProvince: String): List<Commune> {
        return repo.getCommunesByProvince(idProvince)
    }
}
