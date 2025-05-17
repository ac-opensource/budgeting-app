package dev.pandesal.sbp.data.local

import android.content.Context
import androidx.compose.ui.input.key.type
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dev.pandesal.sbp.data.dao.DatabaseDaos
import dev.pandesal.sbp.data.dao.AccountDao
import dev.pandesal.sbp.data.dao.CategoryDao
import dev.pandesal.sbp.data.dao.TransactionDao
import dev.pandesal.sbp.data.local.AccountEntity
import kotlinx.serialization.json.Json
import java.math.BigDecimal

@Database(
    entities = [
        CategoryGroupEntity::class,
        CategoryEntity::class,
        MonthlyBudgetEntity::class,
        TransactionEntity::class,
        AccountEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BigDecimalConverter::class, ListStringConverter::class)
abstract class SbpDatabase : RoomDatabase(), DatabaseDaos {

    abstract override fun categoryDao(): CategoryDao
    abstract override fun transactionDao(): TransactionDao
    abstract override fun accountDao(): AccountDao

    companion object {

        @Volatile
        private var INSTANCE: SbpDatabase? = null

        fun getInstance(context: Context): SbpDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SbpDatabase::class.java,
                        "sbp_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

class BigDecimalConverter {
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toPlainString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }
}

class ListStringConverter {
    @TypeConverter
    fun fromListString(list: List<String>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toListString(jsonString: String): List<String> {
        return Json.decodeFromString(jsonString)
    }
}