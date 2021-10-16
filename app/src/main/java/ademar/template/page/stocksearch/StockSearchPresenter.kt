package ademar.template.page.stocksearch

import ademar.template.R
import ademar.template.arch.ArchErrorMapper
import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.COMPUTATION
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject.createDefault
import io.reactivex.rxjava3.subjects.Subject
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class StockSearchPresenter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val interactor: StockSearchInteractor,
    private val subscriptions: CompositeDisposable,
    @QualifiedScheduler(COMPUTATION) private val computationScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) : ArchErrorMapper<Contract.Model> by ArchErrorMapper.Impl(Contract.Model::Error) {

    val output: Subject<Contract.Model> = createDefault(Contract.Model.Loading)

    fun bind() {
        subscriptions.add(
            interactor.output
                .subscribeOn(computationScheduler)
                .observeOn(mainThreadScheduler)
                .map(::map)
                .onErrorResumeNext(::mapError)
                .subscribe(output::onNext, Timber::e)
        )
    }

    fun unbind() {
        subscriptions.clear()
    }

    private fun map(state: Contract.State): Contract.Model {
        return when (state) {
            is Contract.State.NoSearch -> Contract.Model.Empty(
                context.getString(R.string.stocks_search_empty),
            )
            is Contract.State.Searching -> Contract.Model.Loading
            is Contract.State.ErrorState -> Contract.Model.Error(
                state.message,
            )
            is Contract.State.SearchResult -> {
                if (state.symbols.isEmpty()) {
                    Contract.Model.Empty(
                        context.getString(R.string.stocks_search_not_found),
                    )
                } else {
                    Contract.Model.DataModel(state.symbols)
                }
            }
        }
    }

}