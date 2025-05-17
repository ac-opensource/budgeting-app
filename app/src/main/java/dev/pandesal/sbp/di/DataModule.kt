package dev.pandesal.sbp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pandesal.sbp.data.dao.CategoryDao
import dev.pandesal.sbp.data.dao.TransactionDao
import dev.pandesal.sbp.data.local.SbpDatabase
import dev.pandesal.sbp.data.repository.CategoryRepository
import dev.pandesal.sbp.data.repository.TransactionRepository
import dev.pandesal.sbp.domain.repository.CategoryRepositoryInterface
import dev.pandesal.sbp.domain.repository.TransactionRepositoryInterface
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {


    @Singleton
    @Provides
    fun providesTipDatabase(
        @ApplicationContext context: Context
    ): SbpDatabase {
        return SbpDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideCategoryDao(database: SbpDatabase): CategoryDao {
        return database.categoryDao()
    }
    @Singleton
    @Provides
    fun provideTransactionDao(database: SbpDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Singleton
    @Provides
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
    ): CategoryRepositoryInterface {
        return CategoryRepository(categoryDao)
    }

    @Singleton
    @Provides
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        categoryDao: CategoryDao
    ): TransactionRepositoryInterface {
        return TransactionRepository(transactionDao, categoryDao)
    }


}