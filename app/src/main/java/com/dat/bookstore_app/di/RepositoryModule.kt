package com.dat.bookstore_app.di

import com.dat.bookstore_app.data.repositoryImpl.AuthRepositoryImpl
import com.dat.bookstore_app.data.repositoryImpl.BookRepositoryImpl
import com.dat.bookstore_app.data.repositoryImpl.CartRepositoryImpl
import com.dat.bookstore_app.data.repositoryImpl.CategoryRepositoryImpl
import com.dat.bookstore_app.data.repositoryImpl.FavoriteRepositoryImpl
import com.dat.bookstore_app.data.repositoryImpl.OrderRepositoryImpl
import com.dat.bookstore_app.data.repositoryImpl.PaymentRepositoryImpl
import com.dat.bookstore_app.data.repositoryImpl.SearchRepositoryImpl
import com.dat.bookstore_app.data.repositoryImpl.UserRepositoryImpl
import com.dat.bookstore_app.domain.repository.AuthRepository
import com.dat.bookstore_app.domain.repository.BookRepository
import com.dat.bookstore_app.domain.repository.CartRepository
import com.dat.bookstore_app.domain.repository.CategoryRepository
import com.dat.bookstore_app.domain.repository.FavoriteRepository
import com.dat.bookstore_app.domain.repository.OrderRepository
import com.dat.bookstore_app.domain.repository.PaymentRepository
import com.dat.bookstore_app.domain.repository.SearchRepository
import com.dat.bookstore_app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindBookRepository(bookRepositoryImpl: BookRepositoryImpl): BookRepository

    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindCartRepository(cartRepositoryImpl: CartRepositoryImpl): CartRepository

    @Binds
    abstract fun bindSearchRepository(searchRepositoryImpl: SearchRepositoryImpl): SearchRepository

    @Binds
    abstract fun bindCategoryRepository(categoryRepositoryImpl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    abstract fun bindOrderRepository(orderRepositoryImpl: OrderRepositoryImpl): OrderRepository

    @Binds
    abstract fun bindPaymentRepository(paymentRepositoryImpl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    abstract fun bindFavoriteRepository(favoriteRepositoryImpl: FavoriteRepositoryImpl): FavoriteRepository
}