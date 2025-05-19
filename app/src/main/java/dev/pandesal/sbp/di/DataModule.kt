package dev.pandesal.sbp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.pandesal.sbp.data.dao.CategoryDao
import dev.pandesal.sbp.data.dao.TransactionDao
import dev.pandesal.sbp.data.dao.AccountDao
import dev.pandesal.sbp.data.dao.GoalDao
import dev.pandesal.sbp.data.local.SbpDatabase
import dev.pandesal.sbp.data.repository.CategoryRepository
import dev.pandesal.sbp.data.repository.TransactionRepository
import dev.pandesal.sbp.data.repository.AccountRepository
import dev.pandesal.sbp.data.repository.GoalRepository
import dev.pandesal.sbp.data.repository.SettingsRepository
import dev.pandesal.sbp.data.repository.RecurringTransactionRepository
import dev.pandesal.sbp.domain.repository.CategoryRepositoryInterface
import dev.pandesal.sbp.domain.repository.TransactionRepositoryInterface
import dev.pandesal.sbp.domain.repository.AccountRepositoryInterface
import dev.pandesal.sbp.domain.repository.GoalRepositoryInterface
import dev.pandesal.sbp.domain.repository.SettingsRepositoryInterface
import dev.pandesal.sbp.domain.repository.RecurringTransactionRepositoryInterface
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {


    @Singleton
    @Provides
    fun provideSbpDatabase(
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
    fun provideGoalDao(database: SbpDatabase): GoalDao {
        return database.goalDao()
    }

    @Singleton
    @Provides
    fun provideAccountDao(database: SbpDatabase): AccountDao {
        return database.accountDao()
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

    @Singleton
    @Provides
    fun provideAccountRepository(
        accountDao: AccountDao
    ): AccountRepositoryInterface {
        return AccountRepository(accountDao)
    }

    @Singleton
    @Provides
    fun provideGoalRepository(
        goalDao: GoalDao
    ): GoalRepositoryInterface {
        return GoalRepository(goalDao)
    }

    @Singleton
    @Provides
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepositoryInterface {
        return SettingsRepository(context)
    }

    @Singleton
    @Provides
    fun provideRecurringTransactionRepository(): RecurringTransactionRepositoryInterface {
        return RecurringTransactionRepository()
    }


}