package dev.pandesal.sbp.data.dao

interface DatabaseDaos {
    fun categoryDao(): CategoryDao
    fun transactionDao(): TransactionDao
    fun accountDao(): AccountDao
    fun goalDao(): GoalDao
    fun recurringTransactionDao(): RecurringTransactionDao
}