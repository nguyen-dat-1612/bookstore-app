package com.dat.bookstore_app.data.datasource.local.datastore

import android.util.Log
import com.dat.bookstore_app.data.datasource.remote.api.AuthApiNoAuth
import com.dat.bookstore_app.network.Result
import com.dat.bookstore_app.utils.extension.apiCallResponse
import com.plus.baseandroidapp.data.datasource.datastore.UserManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class TokenProvider @Inject constructor(
    private val userManager: UserManager,
    private val authApi: AuthApiNoAuth
) {
    @Volatile
    private var cachedToken: String? = null

    suspend fun preload() {
        cachedToken = userManager.getAccessTokenOnce()
    }

    fun getToken(): String? = cachedToken

    suspend fun updateToken(newToken: String) {
        cachedToken = newToken
        userManager.saveAccessToken(newToken)
    }

    suspend fun clearToken() {
        cachedToken = null
        userManager.clearTokens().first()
    }

    suspend fun refreshToken(): String? {
        return try {
            val result = apiCallResponse {
                authApi.getRefreshToken()
            }
            when (result) {
                is Result.Success -> {
                    val newToken = result.data?.accessToken
                    if (!newToken.isNullOrBlank()) {
                        updateToken(newToken)
                        newToken
                    } else null
                }

                is Result.Error -> {
                    Log.e("TokenProvider", "Error refreshing token: ${result.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("TokenProvider", "Exception when refreshing token", e)
            null
        }
    }
}

