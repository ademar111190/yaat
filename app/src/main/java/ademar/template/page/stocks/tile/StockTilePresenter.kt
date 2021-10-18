package ademar.template.page.stocks.tile

import ademar.template.R
import ademar.template.arch.ArchPresenter
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.COMPUTATION
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewScoped
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import javax.inject.Inject

@ViewScoped
class StockTilePresenter @Inject constructor(
    @ApplicationContext private val context: Context,
    subscriptions: CompositeDisposable,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) : ArchPresenter<Contract.State, Contract.Model>(
    errorFactory = { error ->
        Contract.Model.Error(
            error,
            context.getString(R.string.stocks_tile_retry),
        )
    },
    subscriptions = subscriptions,
    backgroundScheduler = computationScheduler,
    foregroundScheduler = mainThreadScheduler,
    output = createDefault(Contract.Model.Loading),
) {

    override fun map(state: Contract.State): Contract.Model = when (state) {
        is Contract.State.InquiryState -> Contract.Model.Loading
        is Contract.State.DeletedState -> Contract.Model.Deleted(
            context.getString(R.string.stocks_tile_deleted, state.symbol),
            context.getString(R.string.stocks_tile_readd),
        )
        is Contract.State.NoSymbol -> Contract.Model.Loading
        is Contract.State.ErrorState -> Contract.Model.Error(
            state.message,
            context.getString(R.string.stocks_tile_retry),
        )
        is Contract.State.DataState -> {
            if (state.value == 0.0) {
                Contract.Model.Loading
            } else {
                Contract.Model.DataModel(
                    symbol = state.symbol,
                    value = state.value.toString(),
                )
            }
        }
    }

}
