package ademar.template.page.dashboard

import ademar.template.R
import ademar.template.arch.ArchPresenter
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.COMPUTATION
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.page.dashboard.Contract.Model
import ademar.template.page.dashboard.Contract.State
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import javax.inject.Inject

@FragmentScoped
class DashboardPresenter @Inject constructor(
    @ApplicationContext private val context: Context,
    subscriptions: CompositeDisposable,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) : ArchPresenter<State, Model>(
    errorFactory = Model::Error,
    subscriptions = subscriptions,
    backgroundScheduler = computationScheduler,
    foregroundScheduler = mainThreadScheduler,
    output = create(),
) {

    override fun map(state: State): Model {
        return when (state) {
            is State.DataState -> Model.DataModel(
                progressPercentTitle = context.getString(R.string.dashboard_progress_percent_title),
                progressPercent = state.progressPercent.toString(),
                difficultyChangeTitle = context.getString(R.string.dashboard_difficulty_change_title),
                difficultyChange = state.difficultyChange.toString(),
                estimatedRetargetDateTitle = context.getString(R.string.dashboard_estimated_retarget_date_title),
                estimatedRetargetDate = state.estimatedRetargetDate.toString(),
                remainingBlocksTitle = context.getString(R.string.dashboard_remaining_blocks_title),
                remainingBlocks = state.remainingBlocks.toString(),
                remainingTimeTitle = context.getString(R.string.dashboard_remaining_time_title),
                remainingTime = state.remainingTime.toString(),
                previousRetargetTitle = context.getString(R.string.dashboard_previous_retarget_title),
                previousRetarget = state.previousRetarget.toString(),
            )
            is State.ErrorState -> Model.Error(
                state.message,
            )
        }
    }

}
