package ademar.template.page.stocks.tile

import ademar.template.arch.ArchInteractor
import ademar.template.db.AppDatabase
import ademar.template.db.TickerEntity
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import ademar.template.network.api.AlphaVantageService
import ademar.template.network.payload.TimeSeriesDailyResponse
import ademar.template.page.stocks.tile.Contract.Command
import ademar.template.page.stocks.tile.Contract.State
import dagger.hilt.android.scopes.ViewScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.empty
import io.reactivex.rxjava3.core.Observable.just
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import javax.inject.Inject

@ViewScoped
class StockTileInteractor @Inject constructor(
    private val service: AlphaVantageService,
    private val db: AppDatabase,
    subscriptions: CompositeDisposable,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) : ArchInteractor<Command, State>(
    errorFactory = State::ErrorState,
    subscriptions = subscriptions,
    backgroundScheduler = computationScheduler,
    foregroundScheduler = mainThreadScheduler,
    output = createDefault(State.NoSymbol),
) {

    private var lastSymbol = ""

    override fun map(
        command: Command,
    ): Observable<State> = when (command) {
        is Command.Initial -> just(State.NoSymbol)
        is Command.Retry -> {
            output.onNext(State.NoSymbol)
            search(lastSymbol)
        }
        is Command.Bind -> search(command.symbol)
    }

    private fun search(symbol: String): Observable<State> {
        lastSymbol = symbol
        return db.tickerDao().getBySymbol(symbol)
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .map<State> { ticker ->
                State.DataState(
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

    private fun mapResponse(response: TimeSeriesDailyResponse): Observable<State> {
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
            .andThen(just(State.DataState(symbol, value)))
    }

}
