package ademar.template.page.stocks

import ademar.template.R
import ademar.template.arch.ArchPresenter
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.COMPUTATION
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.page.stocks.Contract.Model
import ademar.template.page.stocks.Contract.State
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import javax.inject.Inject

@FragmentScoped
class StockPresenter @Inject constructor(
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
            is State.DataState -> {
                val symbols = state.symbols
                if (symbols.isEmpty()) {
                    Model.Empty(
                        message = context.getString(R.string.stocks_empty),
                    )
                } else {
                    Model.DataModel(
                        items = state.symbols,
                    )
                }
            }
            is State.ErrorState -> Model.Error(
                state.message,
            )
        }
    }

}
