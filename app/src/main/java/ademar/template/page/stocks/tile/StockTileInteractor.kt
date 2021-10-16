package ademar.template.page.stocks.tile

import ademar.template.db.AppDatabase
import ademar.template.db.TickerEntity
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import ademar.template.network.api.AlphaVantageService
import dagger.hilt.android.scopes.ViewScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.just
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@ViewScoped
class StockTileInteractor @Inject constructor(
    private val service: AlphaVantageService,
    private val db: AppDatabase,
    private val subscriptions: CompositeDisposable,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    val output: Subject<Contract.State> = createDefault(Contract.State.NoSymbol)
    private var lastQuery = ""

    fun bind(view: Contract.View) {
        subscriptions.add(
            view.output
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
                .flatMap(::map)
                .subscribe(output::onNext, output::onError)
        )
    }

    fun unbind() {
        subscriptions.clear()
    }

    private fun map(
        command: Contract.Command,
    ): Observable<Contract.State> = when (command) {
        is Contract.Command.Initial -> just(Contract.State.NoSymbol)
        is Contract.Command.Retry -> {
            output.onNext(Contract.State.NoSymbol)
            search(lastQuery)
        }
        is Contract.Command.Bind -> search(command.symbol)
    }

    private fun search(query: String): Observable<Contract.State> {
        lastQuery = query
        return db.tickerDao().getBySymbol(query)
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .map<Contract.State> { ticker ->
                Contract.State.DataState(
                    ticker.symbol,
                    ticker.value,
                )
            }
            .doOnSuccess { state ->
                output.onNext(state)
                subscriptions.add(service.timeSeriesDaily(symbol = query)
                    .subscribeOn(ioScheduler)
                    .observeOn(mainThreadScheduler)
                    .map<Contract.State> { response ->
                        if (response.note != null) throw Exception(response.note)
                        val symbol = response.metaData?.symbol ?: throw Exception("No symbol found")
                        val value = response.timeSeries?.series?.values?.first()
                            ?.close?.toDouble() ?: throw Exception("No value found")

                        db.tickerDao().insert(TickerEntity(symbol = symbol, value = value))

                        Contract.State.DataState(symbol, value)
                    }
                    .subscribe(output::onNext, output::onError))
            }
            .toObservable()
    }

}
