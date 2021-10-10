package ademar.template.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TickerEntity::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tickerDao(): TickerDao

}

class AppDatabaseCreator : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) = db.run {
        beginTransaction()
        try {
            execSQL("INSERT INTO tickers VALUES('IBM', 143.22);")
            execSQL("INSERT INTO tickers VALUES('MGLU3.SA', 14.97);")
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }

}
