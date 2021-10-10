package ademar.template.db

import androidx.room.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Entity(tableName = "tickers")
data class TickerEntity(
    @PrimaryKey val uid: Long,
    @ColumnInfo(name = "symbol") val symbol: String,
)

@Dao
interface TickerDao {

    @Query("SELECT * FROM tickers")
    fun getAll(): Single<List<TickerEntity>>

    @Insert
    fun insert(ticker: TickerEntity): Completable

    @Delete
    fun delete(ticker: TickerEntity): Completable

}
