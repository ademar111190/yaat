package ademar.template.page.settings

import ademar.template.R
import ademar.template.arch.ArchBinder
import ademar.template.page.settings.Contract.Command
import ademar.template.page.settings.Contract.Model
import ademar.template.widget.Reselectable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(), Reselectable, Contract.View {

    @Inject override lateinit var subscriptions: CompositeDisposable
    @Inject lateinit var presenter: SettingsPresenter
    @Inject lateinit var interactor: SettingsInteractor
    @Inject lateinit var archBinder: ArchBinder

    override val output: Subject<Command> = create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.container).setOnClickListener {}
        archBinder.bind(this, interactor, presenter)
    }

    override fun onResume() {
        super.onResume()
        output.onNext(Command.Initial)
    }

    override fun onReselected() = Unit

    override fun render(model: Model) {
        val view = view ?: return
        val error = view.findViewById<TextView>(R.id.settings_error)
        val load = view.findViewById<ProgressBar>(R.id.settings_load)

        when (model) {

            is Model.Loading -> {
                load.visibility = VISIBLE
                error.visibility = GONE
            }

            is Model.DataModel -> {
                load.visibility = GONE
                error.visibility = VISIBLE

                error.text = model.toString()
            }

            is Model.Error -> {
                load.visibility = GONE
                error.visibility = VISIBLE

                error.text = model.message
            }

        }
    }

}
