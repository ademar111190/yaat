package ademar.template.page.stocksearch

import ademar.template.R
import ademar.template.arch.ArchBinder
import ademar.template.page.stocksearch.Contract.Command
import ademar.template.page.stocksearch.Contract.Model
import ademar.template.widget.AfterTextChanged
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent.*
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject.create
import io.reactivex.rxjava3.subjects.Subject
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

@AndroidEntryPoint
class StockSearchActivity : AppCompatActivity(), Contract.View {

    @Inject override lateinit var subscriptions: CompositeDisposable
    @Inject lateinit var presenter: StockSearchPresenter
    @Inject lateinit var interactor: StockSearchInteractor
    @Inject lateinit var archBinder: ArchBinder

    override val output: Subject<Command> = create()

    private val termEmitter: Subject<String> = create()
    private val adapter = StockSearchAdapter {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_stock_search)

        val searchField = findViewById<EditText>(R.id.search_field)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        searchField.addTextChangedListener(AfterTextChanged(::onSearchChanged))

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.stock_search_menu_clear -> {
                    searchField.setText("")
                }
                R.id.stock_search_menu_voice -> {
                    val intent = Intent(ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM)
                    intent.putExtra(EXTRA_PROMPT, getString(R.string.stocks_search_voice))
                    startActivityForResult(intent, 1)
                }
                else -> null
            } != null
        }
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<RecyclerView>(R.id.list).adapter = adapter
        archBinder.bind(this, interactor, presenter)
    }

    override fun onResume() {
        super.onResume()
        subscriptions.add(
            termEmitter
                .throttleWithTimeout(1000, MILLISECONDS)
                .map(Command::Search)
                .subscribe(output::onNext, Timber::e)
        )
        output.onNext(Command.Initial)
        onSearchChanged(findViewById<EditText>(R.id.search_field).text.toString())
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val term = data.getStringArrayListExtra(EXTRA_RESULTS)?.firstOrNull()
            if (term != null) {
                output.onNext(Command.VoiceSearch(term))
            }
        }
    }

    private fun onSearchChanged(term: String) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val clear = toolbar.menu.findItem(R.id.stock_search_menu_clear)
        val voice = toolbar.menu.findItem(R.id.stock_search_menu_voice)
        if (term.isEmpty()) {
            clear.isVisible = false
            voice.isVisible = true
        } else {
            clear.isVisible = true
            voice.isVisible = false
        }
        termEmitter.onNext(term)
    }

    override fun render(model: Model) {
        val load = findViewById<ProgressBar>(R.id.load)
        val error = findViewById<TextView>(R.id.error)
        val list = findViewById<RecyclerView>(R.id.list)

        when (model) {
            is Model.Loading -> {
                load.visibility = View.VISIBLE
                error.visibility = View.GONE
                list.visibility = View.GONE
            }

            is Model.Error -> {
                load.visibility = View.GONE
                error.visibility = View.VISIBLE
                list.visibility = View.GONE
                error.text = model.message
            }

            is Model.Empty -> {
                load.visibility = View.GONE
                error.visibility = View.VISIBLE
                list.visibility = View.GONE
                error.text = model.message
            }

            is Model.DataModel -> {
                load.visibility = View.GONE
                error.visibility = View.GONE
                list.visibility = View.VISIBLE
                adapter.setItems(model.items)
            }
        }
    }

}
