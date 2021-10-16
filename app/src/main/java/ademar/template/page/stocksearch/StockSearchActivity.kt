package ademar.template.page.stocksearch

import ademar.template.R
import ademar.template.widget.AfterTextChanged
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent.*
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject.create
import io.reactivex.rxjava3.subjects.Subject
import javax.inject.Inject

@AndroidEntryPoint
class StockSearchActivity : AppCompatActivity(), Contract.View {

    @Inject lateinit var subscriptions: CompositeDisposable
    @Inject lateinit var interactor: StockSearchInteractor

    override val output: Subject<Contract.Command> = create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_stock_search)

        val searchField = findViewById<EditText>(R.id.search_field)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        searchField.addTextChangedListener(AfterTextChanged { term ->
            val clear = toolbar.menu.findItem(R.id.stock_search_menu_clear)
            val voice = toolbar.menu.findItem(R.id.stock_search_menu_voice)
            if (term.isEmpty()) {
                clear.isVisible = false
                voice.isVisible = true
            } else {
                clear.isVisible = true
                voice.isVisible = false
            }
            output.onNext(Contract.Command.Search(term))
        })

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

        interactor.bind(this)
        output.onNext(Contract.Command.Initial)
    }

    override fun onDestroy() {
        super.onDestroy()
        interactor.unbind()
        subscriptions.clear()
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
                output.onNext(Contract.Command.VoiceSearch(term))
            }
        }
    }

}
