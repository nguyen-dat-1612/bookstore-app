package com.dat.bookstore_app.di

import android.content.Context
import com.dat.bookstore_app.data.datasource.local.datastore.EncryptionManager
import com.dat.bookstore_app.data.datasource.local.datastore.SearchHistoryManager
import com.dat.bookstore_app.data.datasource.local.datastore.UserManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Singleton
    @Provides
    fun provideEncryptionManager(): EncryptionManager {
        return EncryptionManager()
    }

    @Singleton
    @Provides
    fun provideSecureTokenManager(
        encryptionManager: EncryptionManager,
        @ApplicationContext context: Context
    ): UserManager {
        return UserManager(encryptionManager, context)
    }

    @Singleton
    @Provides
    fun provideSearchHistoryManager(
        @ApplicationContext context: Context
    ): SearchHistoryManager {
        return SearchHistoryManager(context)
    }
}