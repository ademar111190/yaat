package ademar.template.page.stocks

import ademar.template.db.AppDatabase
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.*
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@FragmentScoped
class StockInteractor @Inject constructor(
    private val db: AppDatabase,
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
            .map { symbols ->
                Contract.State(symbols)
            }
            .toObservable()
    }

}
