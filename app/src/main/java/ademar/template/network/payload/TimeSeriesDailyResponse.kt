package ademar.template.network.payload

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TimeSeriesDailyResponse(
    @Json(name = "Meta Data") val metaData: MetaData?,
)

@JsonClass(generateAdapter = true)
data class MetaData(
    @Json(name = "1. Information") val information: String?,
    @Json(name = "2. Symbol") val symbol: String?,
    @Json(name = "3. Last Refreshed") val lastRefreshed: String?,
    @Json(name = "4. Output Size") val outputSize: String?,
    @Json(name = "5. Time Zone") val timeZone: String?,
)
