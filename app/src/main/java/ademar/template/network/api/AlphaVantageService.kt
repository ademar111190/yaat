package ademar.template.network.api

import ademar.template.BuildConfig
import ademar.template.network.payload.TimeSeriesDailyResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageService {

    @GET("query")
    fun timeSeriesDaily(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("apikey") apikey: String = BuildConfig.ALPHA_VANTAGE_KEY,
        @Query("symbol") symbol: String,
    ): Single<TimeSeriesDailyResponse>

}
