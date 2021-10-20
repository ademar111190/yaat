package ademar.template.page.dashboard

import ademar.template.R
import ademar.template.arch.ArchBinder
import ademar.template.page.dashboard.Contract.Command
import ademar.template.page.dashboard.Contract.Model
import ademar.template.widget.Reselectable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment(), Reselectable, Contract.View {

    @Inject override lateinit var subscriptions: CompositeDisposable
    @Inject lateinit var presenter: DashboardPresenter
    @Inject lateinit var interactor: DashboardInteractor
    @Inject lateinit var archBinder: ArchBinder

    override val output: Subject<Command> = BehaviorSubject.create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.container).setOnClickListener {}
        view.findViewById<Toolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.dashboard_menu_reload -> {
                    output.onNext(Command.Reload)
                }
                else -> null
            } != null
        }
        archBinder.bind(this, interactor, presenter)
    }

    override fun onResume() {
        super.onResume()
        output.onNext(Command.Initial)
    }

    override fun onReselected() {
        output.onNext(Command.Reload)
    }

    override fun render(model: Model) {
        val view = view ?: return
        val error = view.findViewById<TextView>(R.id.dashboard_error)
        val progressPercentTitle = view.findViewById<TextView>(R.id.dashboard_progress_percent_title)
        val progressPercentValue = view.findViewById<TextView>(R.id.dashboard_progress_percent_value)
        val difficultyChangeTitle = view.findViewById<TextView>(R.id.dashboard_difficulty_change_title)
        val difficultyChangeValue = view.findViewById<TextView>(R.id.dashboard_difficulty_change_value)
        val estimatedRetargetDateTitle = view.findViewById<TextView>(R.id.dashboard_estimated_retarget_date_title)
        val estimatedRetargetDateValue = view.findViewById<TextView>(R.id.dashboard_estimated_retarget_date_value)
        val remainingBlocksTitle = view.findViewById<TextView>(R.id.dashboard_remaining_blocks_title)
        val remainingBlocksValue = view.findViewById<TextView>(R.id.dashboard_remaining_blocks_value)
        val remainingTimeTitle = view.findViewById<TextView>(R.id.dashboard_remaining_time_title)
        val remainingTimeValue = view.findViewById<TextView>(R.id.dashboard_remaining_time_value)
        val previousRetargetTitle = view.findViewById<TextView>(R.id.dashboard_previous_retarget_title)
        val previousRetargetValue = view.findViewById<TextView>(R.id.dashboard_previous_retarget_value)

        when (model) {
            is Model.DataModel -> {
                error.visibility = GONE
                progressPercentTitle.visibility = VISIBLE
                progressPercentValue.visibility = VISIBLE
                difficultyChangeTitle.visibility = VISIBLE
                difficultyChangeValue.visibility = VISIBLE
                estimatedRetargetDateTitle.visibility = VISIBLE
                estimatedRetargetDateValue.visibility = VISIBLE
                remainingBlocksTitle.visibility = VISIBLE
                remainingBlocksValue.visibility = VISIBLE
                remainingTimeTitle.visibility = VISIBLE
                remainingTimeValue.visibility = VISIBLE
                previousRetargetTitle.visibility = VISIBLE
                previousRetargetValue.visibility = VISIBLE

                progressPercentTitle.text = model.progressPercentTitle
                progressPercentValue.text = model.progressPercent
                difficultyChangeTitle.text = model.difficultyChangeTitle
                difficultyChangeValue.text = model.difficultyChange
                estimatedRetargetDateTitle.text = model.estimatedRetargetDateTitle
                estimatedRetargetDateValue.text = model.estimatedRetargetDate
                remainingBlocksTitle.text = model.remainingBlocksTitle
                remainingBlocksValue.text = model.remainingBlocks
                remainingTimeTitle.text = model.remainingTimeTitle
                remainingTimeValue.text = model.remainingTime
                previousRetargetTitle.text = model.previousRetargetTitle
                previousRetargetValue.text = model.previousRetarget
            }

            is Model.Error -> {
                error.visibility = VISIBLE
                progressPercentTitle.visibility = GONE
                progressPercentValue.visibility = GONE
                difficultyChangeTitle.visibility = GONE
                difficultyChangeValue.visibility = GONE
                estimatedRetargetDateTitle.visibility = GONE
                estimatedRetargetDateValue.visibility = GONE
                remainingBlocksTitle.visibility = GONE
                remainingBlocksValue.visibility = GONE
                remainingTimeTitle.visibility = GONE
                remainingTimeValue.visibility = GONE
                previousRetargetTitle.visibility = GONE
                previousRetargetValue.visibility = GONE

                error.text = model.message
            }
        }
    }

}
