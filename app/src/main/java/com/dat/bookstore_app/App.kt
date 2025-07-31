package com.dat.bookstore_app

import android.app.Application
import com.dat.bookstore_app.data.datasource.local.datastore.TokenProvider
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var tokenProvider: TokenProvider

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            tokenProvider.preload()
        }
    }
}