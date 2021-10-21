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
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
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

    private val decimal = DecimalFormat("0.00%")
    private val date = SimpleDateFormat("dd MMM yyyy", Locale.US)
    private val time = SimpleDateFormat("D'd' hh:mm", Locale.US)

    override fun map(state: State): Model {
        return when (state) {
            is State.DataState -> Model.DataModel(
                progressPercentTitle = context.getString(R.string.dashboard_progress_percent_title),
                progressPercent = decimal.format(state.progressPercent / 100),
                difficultyChangeTitle = context.getString(R.string.dashboard_difficulty_change_title),
                difficultyChange = decimal.format(state.difficultyChange / 100),
                estimatedRetargetDateTitle = context.getString(R.string.dashboard_estimated_retarget_date_title),
                estimatedRetargetDate = fromDateTime(date, state.estimatedRetargetDate),
                remainingBlocksTitle = context.getString(R.string.dashboard_remaining_blocks_title),
                remainingBlocks = state.remainingBlocks.toString(),
                remainingTimeTitle = context.getString(R.string.dashboard_remaining_time_title),
                remainingTime = fromDateTime(time, state.remainingTime),
                previousRetargetTitle = context.getString(R.string.dashboard_previous_retarget_title),
                previousRetarget = decimal.format(state.previousRetarget / 100),
            )
            is State.ErrorState -> Model.Error(
                state.message,
            )
        }
    }

    private fun fromDateTime(formatter: SimpleDateFormat, timestamp: Double): String {
        return formatter.format(Date(timestamp.toLong() * 1000L))
    }

}
