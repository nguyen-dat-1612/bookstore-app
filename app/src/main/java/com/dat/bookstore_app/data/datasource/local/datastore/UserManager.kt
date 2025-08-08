package com.plus.baseandroidapp.data.datasource.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dat.bookstore_app.data.datasource.remote.dto.UserLogin
import com.dat.bookstore_app.domain.models.User
import com.dat.bookstore_app.utils.converter.MoshiProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "secure-app")

@Singleton
class UserManager @Inject constructor(
    private val encryptionManager: EncryptionManager,
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun saveUser(user: User) {
        val userJson = json.encodeToString(User.serializer(), user)
        dataStore.edit { prefs ->
            prefs[KEY_USER_INFORMATION] = userJson
        }
    }

    suspend fun getUser(): Result<UserLogin> = runCatching {
        val prefs = dataStore.data.first()
        val userJson = prefs[KEY_USER_INFORMATION] ?: throw Exception("Chưa có thông tin người dùng")
        MoshiProvider.adapter<UserLogin>().fromJson(userJson) ?: throw Exception("Không parse được user")
    }

    suspend fun clearUser() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_USER_INFORMATION)
        }
    }
    suspend fun saveAccessToken(token: String) {
        Log.d("UserManager", "Saving access token: $token")
        val encryptedToken = encryptionManager.encrypt(token)
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = encryptedToken
        }
    }


    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_ACCESS_TOKEN]?.let {
                encryptionManager.decrypt(it)
            }
        }
    }
    suspend fun getAccessTokenOnce(): String? {
        return dataStore.data
            .map { prefs -> prefs[KEY_ACCESS_TOKEN]?.let { encryptionManager.decrypt(it) } }
            .first()
    }

    fun clearTokens(): Flow<Unit> = flow {
        dataStore.edit { preferences ->
            preferences.remove(KEY_ACCESS_TOKEN)
        }
    }
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_USER_INFORMATION = stringPreferencesKey("user_information")
    }
}
