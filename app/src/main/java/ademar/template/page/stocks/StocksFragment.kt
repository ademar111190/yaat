package ademar.template.page.stocks

import ademar.template.R
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.IO
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.network.api.AlphaVantageService
import ademar.template.page.stocks.Contract.Command.Initial
import ademar.template.widget.Reselectable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@AndroidEntryPoint
class StocksFragment : Fragment(), Reselectable, Contract.View {

    @Inject lateinit var service: AlphaVantageService
    @[Inject QualifiedScheduler(IO)] lateinit var ioScheduler: Scheduler
    @[Inject QualifiedScheduler(MAIN_THREAD)] lateinit var mtScheduler: Scheduler

    override val output: Subject<Contract.Command> = createDefault(Initial)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_stocks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.container).setOnClickListener {}
        service.timeSeriesDaily(symbol = "IBM")
            .subscribeOn(ioScheduler)
            .observeOn(mtScheduler)
            .subscribe({
                view.findViewById<TextView>(R.id.text).text = it.toString()
            }, {
                view.findViewById<TextView>(R.id.text).text = it.toString()
                it.printStackTrace()
            })
    }

    override fun onReselected() = Unit

}
