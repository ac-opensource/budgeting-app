package dev.pandesal.sbp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dev.pandesal.sbp.data.dao.DatabaseDaos
import java.math.BigDecimal

@Database(
    entities = [
        CategoryGroupEntity::class,
        CategoryEntity::class,
        MonthlyBudgetEntity::class,
        TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BigDecimalConverter::class)
abstract class SbpDatabase : RoomDatabase(), DatabaseDaos {

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