package ademar.template.page.stocksearch

import ademar.template.R
import ademar.template.arch.ArchPresenter
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.COMPUTATION
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.page.stocksearch.Contract.Model
import ademar.template.page.stocksearch.Contract.State
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import javax.inject.Inject

@ActivityScoped
class StockSearchPresenter @Inject constructor(
    @ApplicationContext private val context: Context,
    subscriptions: CompositeDisposable,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) : ArchPresenter<State, Model>(
    errorFactory = Model::Error,
    subscriptions = subscriptions,
    backgroundScheduler = computationScheduler,
    foregroundScheduler = mainThreadScheduler,
    output = createDefault(Model.Loading),
) {

    override fun map(state: State): Model {
        return when (state) {
            is State.NoSearch -> Model.Empty(
                context.getString(R.string.stocks_search_empty),
            )
            is State.Searching -> Model.Loading
            is State.ErrorState -> Model.Error(
                state.message,
            )
            is State.SearchResult -> {
                if (state.symbols.isEmpty()) {
                    Model.Empty(
                        context.getString(R.string.stocks_search_not_found),
                    )
                } else {
                    Model.DataModel(state.symbols)
                }
            }
        }
    }

}
