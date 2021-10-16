package ademar.template.page.stocks.tile

import ademar.template.arch.ArchErrorMapper
import ademar.template.db.AppDatabase
import ademar.template.db.TickerEntity
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import ademar.template.network.api.AlphaVantageService
import ademar.template.network.payload.TimeSeriesDailyResponse
import dagger.hilt.android.scopes.ViewScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.empty
import io.reactivex.rxjava3.core.Observable.just
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import io.reactivex.rxjava3.subjects.Subject
import timber.log.Timber
import javax.inject.Inject

@ViewScoped
class StockTileInteractor @Inject constructor(
    private val service: AlphaVantageService,
    private val db: AppDatabase,
    private val subscriptions: CompositeDisposable,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) : ArchErrorMapper<Contract.State> by ArchErrorMapper.Impl(Contract.State::ErrorState) {

    val output: Subject<Contract.State> = createDefault(Contract.State.NoSymbol)

    private var lastSymbol = ""

    fun bind(view: Contract.View) {
        subscriptions.add(
            view.output
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
                .flatMap(::map)
                .onErrorResumeNext(::mapError)
                .subscribe(output::onNext, Timber::e)
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
            search(lastSymbol)
        }
        is Contract.Command.Bind -> search(command.symbol)
    }

    private fun search(symbol: String): Observable<Contract.State> {
        lastSymbol = symbol
        return db.tickerDao().getBySymbol(symbol)
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .map<Contract.State> { ticker ->
                Contract.State.DataState(
                    ticker.symbol,
                    ticker.value,
                )
            }
            .flatMapObservable { state ->
                output.onNext(state)
                service.timeSeriesDaily(symbol = symbol)
                    .subscribeOn(ioScheduler)
                    .observeOn(mainThreadScheduler)
                    .flatMapObservable(::mapResponse)
                    .onErrorResumeNext(::mapError)
            }
            .onErrorResumeNext(::mapError)
    }

    private fun mapResponse(response: TimeSeriesDailyResponse): Observable<Contract.State> {
        if (response.note != null) {
            // api usage exceed (5 calls per minute and 500 calls per day), stop the update temporarily
            return empty()
        }

        val symbol = response.metaData?.symbol ?: return mapError(Exception("No symbol found"))
        val value = response.timeSeries?.series?.values?.first()?.close?.toDouble()
            ?: return mapError(Exception("No value found"))

        return db.tickerDao()
            .insert(TickerEntity(symbol = symbol, value = value))
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .andThen(just(Contract.State.DataState(symbol, value)))
    }

}
