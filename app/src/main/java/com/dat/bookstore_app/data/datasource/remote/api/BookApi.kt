package com.dat.bookstore_app.data.datasource.remote.api

import com.dat.bookstore_app.data.datasource.remote.dto.BookResponseDTO
import com.dat.bookstore_app.data.datasource.remote.dto.PagedBookResponseDTO
import com.dat.bookstore_app.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApi {
    @Headers("No-Authentication: true")
    @GET("books")
    suspend fun getBooks(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
        @Query("filter") filter: String?
    ): ApiResponse<PagedBookResponseDTO>

    @Headers("No-Authentication: true")
    @GET("books/{id}")
    suspend fun getBookById(
        @Path("id") id: Long
    ): ApiResponse<BookResponseDTO>
}