package ademar.template.usecase

import ademar.template.db.AppDatabase
import ademar.template.db.TickerEntity
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.IO
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import dagger.Reusable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@Reusable
class FetchTicker @Inject constructor(
    private val db: AppDatabase,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    fun all(): Single<List<TickerEntity>> = db.tickerDao()
        .getAll()
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)

    fun fromSymbol(
        symbol: String,
    ): Single<TickerEntity> = db.tickerDao()
        .getBySymbol(symbol)
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)

}
