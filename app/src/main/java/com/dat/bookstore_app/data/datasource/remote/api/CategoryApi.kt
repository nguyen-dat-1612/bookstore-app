package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.CategoryResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PagedCategoryResponseDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryApi {
    @GET("categories")
    suspend fun getCategories(): ApiResponse<PagedCategoryResponseDTO>
}