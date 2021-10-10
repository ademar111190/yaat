package ademar.template.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Entity(tableName = "tickers")
data class TickerEntity(
    @PrimaryKey @ColumnInfo(name = "symbol") val symbol: String,
    @ColumnInfo(name = "value") val value: Double,
)

@Dao
interface TickerDao {

    @Query("SELECT * FROM tickers")
    fun getAll(): Single<List<TickerEntity>>

    @Query("SELECT * FROM tickers where symbol = :symbol limit 1")
    fun getBySymbol(symbol: String): Single<TickerEntity>

    @Insert(onConflict = REPLACE)
    fun insert(ticker: TickerEntity): Completable

    @Delete
    fun delete(ticker: TickerEntity): Completable

}
