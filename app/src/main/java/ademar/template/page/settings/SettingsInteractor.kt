package ademar.template.page.settings

import ademar.template.arch.ArchInteractor
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.COMPUTATION
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.page.settings.Contract.Command
import ademar.template.page.settings.Contract.State
import ademar.template.usecase.InitialPage
import ademar.template.usecase.InitialPage.*
import ademar.template.usecase.InitialPagePreference
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import javax.inject.Inject

@FragmentScoped
class SettingsInteractor @Inject constructor(
    private val initialPagePreference: InitialPagePreference,
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
        is Command.Initial -> initial()
        is Command.ChangeInitialPage -> changeInitialPage(command.newPage)
    }

    private fun initial(): Observable<State> {
        return initialPagePreference.get()
            .map<State> { page ->
                State.DataState(
                    initialPageSelected = page,
                    initialPageOptions = listOf(DASHBOARD, STOCKS, SETTINGS)
                )
            }
            .toObservable()
    }

    private fun changeInitialPage(newPage: InitialPage): Observable<State> {
        initialPagePreference.set(newPage)
        return Observable.just(
            State.DataState(
                initialPageSelected = newPage,
                initialPageOptions = listOf(DASHBOARD, STOCKS, SETTINGS)
            )
        )
    }

}
