package com.dat.bookstore_app.data.repository

import android.content.Context
import com.dat.bookstore_app.R
import com.dat.bookstore_app.domain.models.Commune
import com.dat.bookstore_app.domain.models.Province
import com.dat.bookstore_app.domain.repository.ProvinceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProvinceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ProvinceRepository {

    private val provinces = mutableListOf<Province>()
    private val communes = mutableListOf<Commune>()

    init {
        loadDataFromJson()
    }

    private fun loadDataFromJson() {
        val inputStream = context.resources.openRawResource(R.raw.location)
        val jsonText = inputStream.bufferedReader().use { it.readText() }

        // Đọc object gốc
        val rootObject = JSONObject(jsonText)

        // Lấy mảng province
        val provinceArray = rootObject.getJSONArray("province")
        for (i in 0 until provinceArray.length()) {
            val obj = provinceArray.getJSONObject(i)
            provinces.add(
                Province(
                    idProvince = obj.getString("idProvince"),
                    name = obj.getString("name")
                )
            )
        }

        // Lấy mảng commune
        val communeArray = rootObject.getJSONArray("commune")
        for (i in 0 until communeArray.length()) {
            val obj = communeArray.getJSONObject(i)
            communes.add(
                Commune(
                    idProvince = obj.getString("idProvince"),
                    idCommune = obj.getString("idCommune"),
                    name = obj.getString("name")
                )
            )
        }
    }

    override suspend fun getProvinces(): List<Province> = provinces

    override suspend fun getCommunesByProvince(idProvince: String): List<Commune> {
        return communes.filter { it.idProvince == idProvince }
    }
}
