package ademar.template.arch

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.Subject
import timber.log.Timber

abstract class ArchInteractor<Command : Any, State : Any>(
    private val errorFactory: (String) -> State,
    private val subscriptions: CompositeDisposable,
    private val backgroundScheduler: Scheduler,
    private val foregroundScheduler: Scheduler,
    val output: Subject<State>,
) : ArchErrorMapper<State> by ArchErrorMapper.Impl(errorFactory) {

    fun bind(pipe: Subject<Command>) {
        subscriptions.add(
            pipe
                .subscribeOn(backgroundScheduler)
                .observeOn(foregroundScheduler)
                .flatMap(::map)
                .onErrorResumeNext(::mapError)
                .subscribe(output::onNext, Timber::e)
        )
    }

    fun unbind() {
        subscriptions.clear()
    }

    abstract fun map(command: Command): Observable<State>


}
