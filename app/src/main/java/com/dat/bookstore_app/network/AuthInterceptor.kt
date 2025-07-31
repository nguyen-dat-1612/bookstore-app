package com.dat.bookstore_app.network

import com.dat.bookstore_app.data.datasource.local.datastore.TokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Bỏ qua nếu request không cần auth
        if (request.header("No-Authentication") != null) {
            return chain.proceed(request)
        }

        // Gắn access token nếu có
        val token = tokenProvider.getToken()
        val authenticatedRequest = token?.let {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $it")
                .build()
        } ?: request

        var response = chain.proceed(authenticatedRequest)

        // Nếu bị 401 thì thử refresh token
        if (response.code == 401) {
            response.close() // tránh leak connection

            val newToken = runBlocking {
                tokenProvider.refreshToken()
            }

            return if (!newToken.isNullOrBlank()) {
                val retryRequest = request.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $newToken")
                    .build()
                chain.proceed(retryRequest)
            } else {
                runBlocking {
                    tokenProvider.clearToken()
                }
                throw IOException("Unauthorized - Refresh token failed")
            }
        }

        return response
    }
}
