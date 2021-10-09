package ademar.template.page.settings

import ademar.template.R
import ademar.template.page.settings.Contract.Command.Initial
import ademar.template.widget.Reselectable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import io.reactivex.rxjava3.subjects.Subject

@AndroidEntryPoint
class SettingsFragment : Fragment(), Reselectable, Contract.View {

    override val output: Subject<Contract.Command> = createDefault(Initial)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.container).setOnClickListener {}
    }

    override fun onReselected() = Unit

}
