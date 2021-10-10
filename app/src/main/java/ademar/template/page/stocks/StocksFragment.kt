package ademar.template.page.stocks

import ademar.template.R
import ademar.template.page.stocks.Contract.Command.Initial
import ademar.template.widget.Reselectable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import io.reactivex.rxjava3.subjects.Subject
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class StocksFragment : Fragment(), Reselectable, Contract.View {

    @Inject lateinit var subscriptions: CompositeDisposable
    @Inject lateinit var presenter: StockPresenter
    @Inject lateinit var interactor: StockInteractor

    override val output: Subject<Contract.Command> = createDefault(Initial)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_stocks, container, false)
    }

    override fun onStart() {
        super.onStart()
        presenter.bind()
        interactor.bind(this)
        subscriptions.add(presenter.output.subscribe(::render, Timber::e))
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbind()
        interactor.unbind()
        subscriptions.clear()
    }

    override fun onReselected() = Unit

    private fun render(model: Contract.Model) {
        val view = view ?: return
        val content = view.findViewById<TextView>(R.id.content)
        val load = view.findViewById<ProgressBar>(R.id.load)

        when (model) {
            is Contract.Model.Loading -> {
                content.visibility = View.GONE
                load.visibility = View.VISIBLE
            }

            is Contract.Model.Error -> {
                content.visibility = View.VISIBLE
                load.visibility = View.GONE
                content.text = model.message
            }

            is Contract.Model.Empty -> {
                content.visibility = View.VISIBLE
                load.visibility = View.GONE
                content.text = model.message
            }

            is Contract.Model.DataModel -> {
                content.visibility = View.VISIBLE
                load.visibility = View.GONE
                content.text = model.symbols.joinToString()
            }
        }
    }

}
