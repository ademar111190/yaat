package ademar.template.arch

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.Subject
import timber.log.Timber

abstract class ArchPresenter<State : Any, Model : Any>(
    private val errorFactory: (String) -> Model,
    private val subscriptions: CompositeDisposable,
    private val backgroundScheduler: Scheduler,
    private val foregroundScheduler: Scheduler,
    val output: Subject<Model>,
) : ArchErrorMapper<Model> by ArchErrorMapper.Impl(errorFactory) {

    fun bind(pipe: Subject<State>) {
        subscriptions.add(
            pipe
                .subscribeOn(backgroundScheduler)
                .observeOn(foregroundScheduler)
                .map(::map)
                .onErrorResumeNext(::mapError)
                .subscribe(output::onNext, Timber::e)
        )
    }

    fun unbind() {
        subscriptions.clear()
    }

    abstract fun map(state: State): Model

}
