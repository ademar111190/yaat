package ademar.template.page.stocksearch

import ademar.template.arch.ArchInteractor
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.COMPUTATION
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.page.stocksearch.Contract.Command
import ademar.template.page.stocksearch.Contract.State
import ademar.template.usecase.SaveTicker
import ademar.template.usecase.SearchTicker
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import javax.inject.Inject

@ActivityScoped
class StockSearchInteractor @Inject constructor(
    private val searchTicker: SearchTicker,
    private val saveTicker: SaveTicker,
    private val navigator: StockSearchNavigator,
    subscriptions: CompositeDisposable,
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
        is Command.Search -> search(command.term)
        is Command.ItemSelected -> itemSelected(command.item)
    }

    private fun initial(): Observable<State> {
        return Observable.just(State.NoSearch)
    }

    private fun search(
        term: String,
    ): Observable<State> {
        if (term.isEmpty()) {
            return Observable.just(State.NoSearch)
        }
        output.onNext(State.Searching)
        return searchTicker.bySymbol(term)
            .map<State> { response ->
                State.SearchResult(
                    response.bestMatches?.mapNotNull {
                        val symbol = it?.symbol
                        val name = it?.name
                        val type = it?.type
                        if (symbol != null &&
                            name != null &&
                            type != null
                        ) {
                            Contract.Item(symbol, name, type)
                        } else null
                    } ?: emptyList(),
                )
            }
            .toObservable()
    }

    private fun itemSelected(item: Contract.Item): Observable<State> {
        return saveTicker.justSymbol(item.symbol)
            .doOnComplete {
                navigator.close()
            }
            .andThen(Observable.empty())
    }

}
