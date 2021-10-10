package ademar.template.page.stocks

import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import ademar.template.network.api.AlphaVantageService
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@FragmentScoped
class StockInteractor @Inject constructor(
    private val service: AlphaVantageService,
    private val storage: StockStorage,
    private val subscriptions: CompositeDisposable,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    val output: Subject<Contract.State> = create()

    init {
        subscriptions.add(
            storage.initialState()
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
                .subscribe(output::onNext, output::onError)
        )
    }

    fun bind(view: Contract.View) {
        subscriptions.add(
            view.output
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
                .flatMap(::map)
                .doOnNext(storage::save)
                .subscribe(output::onNext, output::onError)
        )
    }

    fun unbind() {
        subscriptions.clear()
    }

    private fun map(
        command: Contract.Command,
    ): Observable<Contract.State> = when (command) {
        is Contract.Command.Initial -> initial()
    }

    private fun initial(): Observable<Contract.State> {
        return service.timeSeriesDaily(symbol = "IBM")
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .map { response ->
                response.metaData?.symbol ?: throw Exception("No symbol found")
            }
            .map { symbol ->
                Contract.State(listOf(symbol))
            }
            .toObservable()
    }

}
