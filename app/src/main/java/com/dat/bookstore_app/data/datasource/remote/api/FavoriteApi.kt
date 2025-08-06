package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.FavoriteRequestDTO
import com.dat.bookstore_app.data.datasource.remote.dto.FavoriteResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PagedFavoriteResponseDTO
import com.dat.bookstore_app.network.ApiResponse
import com.dat.bookstore_app.network.FavoriteApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FavoriteApi {
    @GET("favorites")
    suspend fun getFavorites(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String? = null
    ): ApiResponse<PagedFavoriteResponseDTO>

    @POST("favorites")
    suspend fun createFavorite(@Body reqDTO: FavoriteRequestDTO): ApiResponse<FavoriteResponseDTO>

    @DELETE("favorites/{id}")
    suspend fun deleteFavorite(@Path("id") id: Long): ApiResponse<Any?>

    @GET("favorites-user-book/{bookId}")
    suspend fun checkFavorite(@Path("bookId") bookId: Long): FavoriteApiResponse
}