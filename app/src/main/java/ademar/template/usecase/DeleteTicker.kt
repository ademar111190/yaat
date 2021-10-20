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
class DeleteTicker @Inject constructor(
    private val db: AppDatabase,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    fun allWithSymbol(
        symbol: String,
    ): Completable = db.tickerDao()
        .delete(TickerEntity(symbol, 0.0))
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)

}
