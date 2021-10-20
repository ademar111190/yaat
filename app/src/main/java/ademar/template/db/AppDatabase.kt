package ademar.template.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        DifficultyAdjustmentEntity::class,
        TickerEntity::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tickerDao(): TickerDao

    abstract fun difficultyAdjustmentDao(): DifficultyAdjustmentDao

}

class AppDatabaseCreator : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) = db.run {
        beginTransaction()
        try {
            execSQL("INSERT INTO tickers VALUES('IBM', 143.22);")
            execSQL("INSERT INTO tickers VALUES('MGLU3.SA', 14.97);")
            execSQL("INSERT INTO difficulty_adjustment VALUES(1, 44.39, 0.98, 1627762478.91, 1121, 665977.62, -4.80);")
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }

}
