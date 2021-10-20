package ademar.template.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Entity(tableName = "difficulty_adjustment")
data class DifficultyAdjustmentEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "progressPercent") val progressPercent: Double,
    @ColumnInfo(name = "difficultyChange") val difficultyChange: Double,
    @ColumnInfo(name = "estimatedRetargetDate") val estimatedRetargetDate: Double,
    @ColumnInfo(name = "remainingBlocks") val remainingBlocks: Int,
    @ColumnInfo(name = "remainingTime") val remainingTime: Double,
    @ColumnInfo(name = "previousRetarget") val previousRetarget: Double,
)

@Dao
interface DifficultyAdjustmentDao {

    @Query("SELECT * FROM difficulty_adjustment")
    fun get(): Single<DifficultyAdjustmentEntity>

    @Insert(onConflict = REPLACE)
    fun insert(ticker: DifficultyAdjustmentEntity): Completable

}
