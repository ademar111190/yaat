package ademar.template.page.stocks

import ademar.template.R
import ademar.template.arch.ArchBinder
import ademar.template.page.stocks.Contract.Command
import ademar.template.page.stocks.Contract.Model
import ademar.template.widget.Reselectable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.create
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@AndroidEntryPoint
class StocksFragment : Fragment(), Reselectable, Contract.View {

    @Inject override lateinit var subscriptions: CompositeDisposable
    @Inject lateinit var presenter: StockPresenter
    @Inject lateinit var interactor: StockInteractor
    @Inject lateinit var archBinder: ArchBinder

    private val adapter = StockAdapter()

    override val output: Subject<Command> = create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.page_stocks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Toolbar>(R.id.toolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.stock_menu_search -> {
                    output.onNext(Command.Search)
                    output.onNext(Command.Initial)
                }
                else -> null
            } != null
        }
        view.findViewById<RecyclerView>(R.id.list).adapter = adapter
        archBinder.bind(this, interactor, presenter)
    }

    override fun onResume() {
        super.onResume()
        output.onNext(Command.Initial)
    }

    override fun onReselected() {
        view?.findViewById<RecyclerView>(R.id.list)?.scrollToPosition(0)
    }

    override fun render(model: Model) {
        val view = view ?: return
        val load = view.findViewById<ProgressBar>(R.id.load)
        val error = view.findViewById<TextView>(R.id.error)
        val list = view.findViewById<RecyclerView>(R.id.list)

        when (model) {
            is Model.Loading -> {
                load.visibility = VISIBLE
                error.visibility = GONE
                list.visibility = GONE
            }

            is Model.Error -> {
                load.visibility = GONE
                error.visibility = VISIBLE
                list.visibility = GONE
                error.text = model.message
            }

            is Model.Empty -> {
                load.visibility = GONE
                error.visibility = VISIBLE
                list.visibility = GONE
                error.text = model.message
            }

            is Model.DataModel -> {
                load.visibility = GONE
                error.visibility = GONE
                list.visibility = VISIBLE
                adapter.setItems(model.items)
            }
        }
    }

}
