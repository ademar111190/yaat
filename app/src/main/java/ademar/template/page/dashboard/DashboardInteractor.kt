package ademar.template.page.dashboard

import ademar.template.arch.ArchInteractor
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.COMPUTATION
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.network.payload.DifficultyAdjustmentResponse
import ademar.template.page.dashboard.Contract.Command
import ademar.template.page.dashboard.Contract.State
import ademar.template.usecase.FetchDifficultyAdjustment
import ademar.template.usecase.SaveDifficultyAdjustment
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.empty
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import io.reactivex.rxjava3.subjects.BehaviorSubject.just
import javax.inject.Inject

@FragmentScoped
class DashboardInteractor @Inject constructor(
    private val fetchDifficultyAdjustment: FetchDifficultyAdjustment,
    private val saveDifficultyAdjustment: SaveDifficultyAdjustment,
    subscriptions: CompositeDisposable,
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
        is Command.Initial -> loadData()
        is Command.Reload -> loadData()
    }

    private fun loadData(): Observable<State> {
        return fetchDifficultyAdjustment.local()
            .flatMapObservable<State> { entity ->
                just(
                    State.DataState(
                        entity.progressPercent,
                        entity.difficultyChange,
                        entity.estimatedRetargetDate,
                        entity.remainingBlocks,
                        entity.remainingTime,
                        entity.previousRetarget,
                    )
                )
            }
            .doOnNext(output::onNext)
            .flatMap {
                fetchDifficultyAdjustment.remote()
                    .flatMapObservable(::mapPayload)
                    .onErrorResumeNext(::mapError)
            }
    }

    private fun mapPayload(response: DifficultyAdjustmentResponse): Observable<State> {
        val progressPercent = response.progressPercent ?: return empty()
        val difficultyChange = response.difficultyChange ?: return empty()
        val estimatedRetargetDate = response.estimatedRetargetDate ?: return empty()
        val remainingBlocks = response.remainingBlocks ?: return empty()
        val remainingTime = response.remainingTime ?: return empty()
        val previousRetarget = response.previousRetarget ?: return empty()

        return saveDifficultyAdjustment(
            progressPercent,
            difficultyChange,
            estimatedRetargetDate,
            remainingBlocks,
            remainingTime,
            previousRetarget,
        ).andThen(
            just(
                State.DataState(
                    progressPercent,
                    difficultyChange,
                    estimatedRetargetDate,
                    remainingBlocks,
                    remainingTime,
                    previousRetarget,
                )
            )
        )
    }

}
