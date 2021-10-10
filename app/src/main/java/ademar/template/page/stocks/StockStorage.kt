package ademar.template.page.stocks

import ademar.template.db.AppDatabase
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.IO
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@FragmentScoped
class StockStorage @Inject constructor(
    private val db: AppDatabase,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    fun initialState(): Single<Contract.State> {
        return db.tickerDao().getAll()
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .map { tickers ->
                tickers.map { it.symbol }
            }
            .map { symbols ->
                Contract.State(symbols)
            }
    }

    fun save(state: Contract.State) {
    }

}
