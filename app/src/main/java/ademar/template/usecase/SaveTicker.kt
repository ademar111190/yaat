package ademar.template.usecase

import ademar.template.db.AppDatabase
import ademar.template.db.TickerEntity
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.IO
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import dagger.Reusable
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

@Reusable
class SaveTicker @Inject constructor(
    private val db: AppDatabase,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    operator fun invoke(
        symbol: String,
        value: Double,
    ): Completable = db.tickerDao()
        .insert(TickerEntity(symbol = symbol, value = value))
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)

    fun justSymbol(
        symbol: String,
    ): Completable = invoke(symbol, 0.0)

}
