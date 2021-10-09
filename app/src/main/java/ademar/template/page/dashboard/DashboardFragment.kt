package ademar.template.page.dashboard

import ademar.template.R
import ademar.template.page.dashboard.Contract.Command.Start
import ademar.template.widget.Reselectable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import io.reactivex.rxjava3.subjects.Subject

class DashboardFragment : Fragment(), Reselectable, Contract.View {

    override val output: Subject<Contract.Command> = createDefault(Start)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.page_dashboard, container, false)
        view.findViewById<View>(R.id.container).setOnClickListener {}
        return view
    }

    override fun onReselected() = Unit

}
