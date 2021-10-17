package ademar.template.page.stocksearch

import ademar.template.arch.ArchInteractor
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import ademar.template.network.api.AlphaVantageService
import ademar.template.page.stocksearch.Contract.Command
import ademar.template.page.stocksearch.Contract.State
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import javax.inject.Inject

@ActivityScoped
class StockSearchInteractor @Inject constructor(
    private val service: AlphaVantageService,
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
        is Command.Search -> search(command.term)
        is Command.VoiceSearch -> search(command.term)
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
        return service.search(keywords = term)
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
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

}
