package com.dat.bookstore_app.data.datasource.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "search_history")
class SearchHistoryManager @Inject constructor(
    @ApplicationContext private val context: Context
){

    private val dataStore = context.dataStore


    private fun parseHistory(rawHistory: String?): MutableList<String> {
        return rawHistory
            ?.split(DELIMITER)
            ?.filter { it.isNotBlank() }
            ?.toMutableList()
            ?: mutableListOf()
    }

    fun getSearchHistory(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            val rawHistory = preferences[SEARCH_HISTORY_KEY] ?: ""
            parseHistory(rawHistory)
        }
    }

    suspend fun saveSearchQuery(query: String) {
        dataStore.edit { preferences ->
            val rawHistory = parseHistory(preferences[SEARCH_HISTORY_KEY])

            rawHistory.remove(query)
            rawHistory.add(0, query)

            if (rawHistory.size > MAX_HISTORY_SIZE) {
                rawHistory.subList(MAX_HISTORY_SIZE, rawHistory.size).clear()
            }

            preferences[SEARCH_HISTORY_KEY] = rawHistory.joinToString(DELIMITER)
        }
    }
    suspend fun clearHistory() {
        dataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_KEY)
        }
    }


    companion object {
        private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history_list")
        private val DELIMITER = "|"
        private val MAX_HISTORY_SIZE = 10
    }
}

