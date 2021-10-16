package ademar.template.page.stocksearch

import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import ademar.template.network.api.AlphaVantageService
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@ActivityScoped
class StockSearchInteractor @Inject constructor(
    private val service: AlphaVantageService,
    private val subscriptions: CompositeDisposable,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    val output: Subject<Contract.State> = create()

    fun bind(view: Contract.View) {
        subscriptions.add(
            view.output
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
                .flatMap(::map)
                .subscribe(output::onNext, ::mapError)
        )
    }

    fun unbind() {
        subscriptions.clear()
    }

    private fun map(
        command: Contract.Command,
    ): Observable<Contract.State> = when (command) {
        is Contract.Command.Initial -> initial()
        is Contract.Command.Search -> search(command.term)
        is Contract.Command.VoiceSearch -> search(command.term)
    }

    private fun initial(): Observable<Contract.State> {
        return Observable.just(Contract.State.NoSearch)
    }

    private fun search(
        term: String,
    ): Observable<Contract.State> {
        if (term.isEmpty()) {
            return Observable.just(Contract.State.NoSearch)
        }
        output.onNext(Contract.State.Searching)
        return service.search(keywords = term)
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .map<Contract.State> { response ->
                Contract.State.SearchResult(
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

    private fun mapError(error: Throwable) {
        output.onNext(
            Contract.State.ErrorState(
                error.localizedMessage ?: error.message ?: "$error"
            )
        )
    }

}
