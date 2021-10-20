package ademar.template.page.stocks.tile

import ademar.template.arch.ArchInteractor
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import ademar.template.ext.valueOrError
import ademar.template.network.payload.TimeSeriesDailyResponse
import ademar.template.page.stocks.tile.Contract.Command
import ademar.template.page.stocks.tile.Contract.State
import ademar.template.usecase.DeleteTicker
import ademar.template.usecase.FetchTicker
import ademar.template.usecase.FetchTimeSeriesDaily
import ademar.template.usecase.SaveTicker
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
    private val fetchTicker: FetchTicker,
    private val saveTicker: SaveTicker,
    private val deleteTicker: DeleteTicker,
    private val fetchTimeSeriesDaily: FetchTimeSeriesDaily,
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
        is Command.Retry -> retry()
        is Command.Bind -> search(command.symbol)
        is Command.Delete -> delete(command.symbol)
    }

    private fun retry(): Observable<State> {
        return output.valueOrError()
            .subscribeOn(ioScheduler)
            .observeOn(mainThreadScheduler)
            .flatMapObservable { lastState ->
                if (lastState is State.DeletedState) {
                    reAdd(lastState.symbol)
                } else {
                    retrySearch()
                }
            }
            .onErrorResumeWith(retrySearch())
    }

    private fun reAdd(symbol: String): Observable<State> {
        output.onNext(State.NoSymbol)
        return saveTicker.justSymbol(symbol)
            .andThen(search(symbol))
    }

    private fun retrySearch(): Observable<State> {
        output.onNext(State.NoSymbol)
        return search(lastSymbol)
    }

    private fun search(symbol: String): Observable<State> {
        lastSymbol = symbol
        return fetchTicker.fromSymbol(symbol)
            .map<State> { ticker ->
                State.DataState(
                    ticker.symbol,
                    ticker.value,
                )
            }
            .flatMapObservable { state ->
                output.onNext(state)
                fetchTimeSeriesDaily.fromSymbol(symbol)
                    .flatMapObservable(::mapResponse)
                    .onErrorResumeNext(::mapError)
            }
            .onErrorResumeNext(::mapError)
    }

    private fun delete(symbol: String): Observable<State> {
        output.onNext(State.NoSymbol)
        return deleteTicker.allWithSymbol(symbol)
            .andThen(just(State.DeletedState(symbol)))
    }

    private fun mapResponse(response: TimeSeriesDailyResponse): Observable<State> {
        if (response.note != null) {
            // api usage exceed (5 calls per minute and 500 calls per day), stop the update temporarily
            return empty()
        }

        val symbol = response.metaData?.symbol ?: return mapError(Exception("No symbol found"))
        val value = response.timeSeries?.series?.values?.first()?.close?.toDouble()
            ?: return mapError(Exception("No value found"))

        return saveTicker(symbol, value)
            .andThen(just(State.DataState(symbol, value)))
    }

}
