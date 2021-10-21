package ademar.template.page.settings

import ademar.template.R
import ademar.template.arch.ArchPresenter
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.COMPUTATION
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.page.settings.Contract.Model
import ademar.template.page.settings.Contract.State
import ademar.template.usecase.InitialPage
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import javax.inject.Inject

@FragmentScoped
class SettingsPresenter @Inject constructor(
    @ApplicationContext private val context: Context,
    subscriptions: CompositeDisposable,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) : ArchPresenter<State, Model>(
    errorFactory = Model::Error,
    subscriptions = subscriptions,
    backgroundScheduler = computationScheduler,
    foregroundScheduler = mainThreadScheduler,
    output = createDefault(Model.Loading),
) {

    override fun map(state: State): Model {
        return when (state) {
            is State.DataState -> Model.DataModel(
                initialPageTitle = context.getString(R.string.settings_option_default_page_title),
                initialPageValue = pageTitle(state.initialPageSelected),
                initialPageOptions = state.initialPageOptions.map { page ->
                    pageTitle(page) to page
                }.toMap(),
            )
            is State.ErrorState -> Model.Error(
                state.message,
            )
        }
    }

    private fun pageTitle(
        page: InitialPage,
    ): String = when (page) {
        InitialPage.DEFAULT, InitialPage.DASHBOARD -> context.getString(R.string.page_title_dashboard)
        InitialPage.STOCKS -> context.getString(R.string.page_title_stocks)
        InitialPage.SETTINGS -> context.getString(R.string.page_title_settings)
    }

}
