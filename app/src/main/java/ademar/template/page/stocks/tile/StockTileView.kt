package ademar.template.page.stocks.tile

import ademar.template.R
import ademar.template.arch.ArchBinder
import ademar.template.page.stocks.tile.Contract.Command
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@AndroidEntryPoint
class StockTileView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs), Contract.View {

    @Inject override lateinit var subscriptions: CompositeDisposable
    @Inject lateinit var presenter: StockTilePresenter
    @Inject lateinit var interactor: StockTileInteractor
    @Inject lateinit var archBinder: ArchBinder

    override val output: Subject<Command> = createDefault(Command.Initial)
    private val popup = StockTilePopup("", output)

    init {
        LayoutInflater.from(context).inflate(R.layout.tile_stock, this, true)
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        findViewById<Button>(R.id.retry).setOnClickListener {
            output.onNext(Command.Retry)
        }
        findViewById<View>(R.id.content).setOnClickListener(popup)
        archBinder.bind(this, interactor, presenter)
    }

    fun bind(symbol: String) {
        output.onNext(Command.Bind(symbol))
        popup.symbol = symbol
    }

    override fun render(model: Contract.Model) {
        val title = findViewById<TextView>(R.id.title)
        val value = findViewById<TextView>(R.id.value)
        val load = findViewById<ProgressBar>(R.id.load)
        val error = findViewById<TextView>(R.id.error)
        val retry = findViewById<Button>(R.id.retry)

        when (model) {

            is Contract.Model.DataModel -> {
                title.visibility = VISIBLE
                value.visibility = VISIBLE
                load.visibility = GONE
                error.visibility = GONE
                retry.visibility = GONE

                title.text = model.symbol
                value.text = model.value
            }

            is Contract.Model.Error -> {
                title.visibility = GONE
                value.visibility = GONE
                load.visibility = GONE
                error.visibility = VISIBLE
                retry.visibility = VISIBLE

                error.text = model.message
                retry.text = model.retry
            }

            is Contract.Model.Deleted -> {
                title.visibility = GONE
                value.visibility = GONE
                load.visibility = GONE
                error.visibility = VISIBLE
                retry.visibility = VISIBLE

                error.text = model.message
                retry.text = model.retry
            }

            is Contract.Model.Loading -> {
                title.visibility = GONE
                value.visibility = GONE
                load.visibility = VISIBLE
                error.visibility = GONE
                retry.visibility = GONE
            }

        }
    }

}
