package com.dat.bookstore_app.domain.repository

import com.dat.bookstore_app.domain.models.Commune
import com.dat.bookstore_app.domain.models.Province

interface ProvinceRepository {
    suspend fun getProvinces(): List<Province>
    suspend fun getCommunesByProvince(idProvince: String): List<Commune>
}
