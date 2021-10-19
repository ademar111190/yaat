package ademar.template.page.stocks

import ademar.template.arch.ArchInteractor
import ademar.template.db.AppDatabase
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import ademar.template.page.stocks.Contract.Command
import ademar.template.page.stocks.Contract.State
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import javax.inject.Inject

@FragmentScoped
class StockInteractor @Inject constructor(
    private val db: AppDatabase,
    private val stockNavigator: StockNavigator,
    subscriptions: CompositeDisposable,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) : ArchInteractor<Command, State>(
    errorFactory = State::ErrorState,
    subscriptions = subscriptions,
    backgroundScheduler = computationScheduler,
    foregroundScheduler = mainThreadScheduler,
    output = create(),
) {

    override fun map(
        command: Command,
    ): Observable<State> = when (command) {
        is Command.Initial -> initial()
        is Command.Search -> search()
    }

    private fun initial(): Observable<State> {
        return db.tickerDao().getAll()
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .map { tickers ->
                tickers.map {
                    Contract.Item(
                        it.symbol,
                        it.value,
                    )
                }
            }
            .map<State> { symbols ->
                State.DataState(symbols.sortedBy { it.symbol })
            }
            .toObservable()
    }

    private fun search(): Observable<State> {
        stockNavigator.openSearch()
        return Observable.empty()
    }

}
