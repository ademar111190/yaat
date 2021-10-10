package ademar.template.page.stocks.tile

import ademar.template.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class StockTileView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs), Contract.View {

    @Inject lateinit var subscriptions: CompositeDisposable
    @Inject lateinit var presenter: StockTilePresenter
    @Inject lateinit var interactor: StockTileInteractor

    override val output: Subject<Contract.Command> = createDefault(Contract.Command.Initial)

    init {
        LayoutInflater.from(context).inflate(R.layout.tile_stock, this, true)
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }

    fun bind(symbol: String) {
        output.onNext(Contract.Command.Bind(symbol))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter.bind()
        interactor.bind(this)
        subscriptions.add(presenter.output.subscribe(::render, Timber::e))
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter.unbind()
        interactor.unbind()
        subscriptions.clear()
    }

    private fun render(model: Contract.Model) {
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

            Contract.Model.Loading -> {
                title.visibility = GONE
                value.visibility = GONE
                load.visibility = VISIBLE
                error.visibility = GONE
                retry.visibility = GONE
            }

        }
    }

}
