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
            execSQL("INSERT INTO tickers VALUES(1, 'IBM');")
            execSQL("INSERT INTO tickers VALUES(2, 'MGLU3.SA');")
            execSQL("INSERT INTO tickers VALUES(3, 'GOLD');")
            setTransactionSuccessful()
        } finally {
            endTransaction()
        }
    }

}
