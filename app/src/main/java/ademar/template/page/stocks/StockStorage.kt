package ademar.template.page.stocks

import dagger.hilt.android.scopes.FragmentScoped
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@FragmentScoped
class StockStorage @Inject constructor() {

    fun initialState(): Single<Contract.State> {
        return Single.just(
            Contract.State(
                listOf(
                    "IBM",
                    "MGLU3.SA",
                )
            )
        )
    }

    fun save(state: Contract.State) {
    }

}
