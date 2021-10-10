package ademar.template.page.stocks

import ademar.template.R
import ademar.template.page.stocks.Contract.Command.Initial
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
import androidx.recyclerview.widget.RecyclerView
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

    private val adapter = StockAdapter();

    override val output: Subject<Contract.Command> = createDefault(Initial)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_stocks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.list).adapter = adapter
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
        val load = view.findViewById<ProgressBar>(R.id.load)
        val error = view.findViewById<TextView>(R.id.error)
        val list = view.findViewById<RecyclerView>(R.id.list)

        when (model) {
            is Contract.Model.Loading -> {
                load.visibility = VISIBLE
                error.visibility = GONE
                list.visibility = GONE
            }

            is Contract.Model.Error -> {
                load.visibility = GONE
                error.visibility = VISIBLE
                list.visibility = GONE
                error.text = model.message
            }

            is Contract.Model.Empty -> {
                load.visibility = GONE
                error.visibility = VISIBLE
                list.visibility = GONE
                error.text = model.message
            }

            is Contract.Model.DataModel -> {
                load.visibility = GONE
                error.visibility = GONE
                list.visibility = VISIBLE
                adapter.setItems(model.items)
            }
        }
    }

}
