package ademar.template.usecase

import ademar.template.di.qualifiers.QualifiedScheduler
import ademar.template.di.qualifiers.QualifiedSchedulerOption.IO
import ademar.template.di.qualifiers.QualifiedSchedulerOption.MAIN_THREAD
import ademar.template.network.api.AlphaVantageService
import ademar.template.network.payload.TimeSeriesDailyResponse
import dagger.Reusable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

@Reusable
class FetchTimeSeriesDaily @Inject constructor(
    private val service: AlphaVantageService,
    @QualifiedScheduler(IO) private val ioScheduler: Scheduler,
    @QualifiedScheduler(MAIN_THREAD) private val mainThreadScheduler: Scheduler,
) {

    fun fromSymbol(
        symbol: String,
    ): Single<TimeSeriesDailyResponse> = service
        .timeSeriesDaily(symbol = symbol)
        .subscribeOn(ioScheduler)
        .observeOn(mainThreadScheduler)

}
