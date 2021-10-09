package ademar.template.network.adapter

import ademar.template.network.payload.TimeSeries
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

class CustomJsonFactory : JsonAdapter.Factory {

    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi,
    ): JsonAdapter<*>? = when (type) {
        TimeSeries::class.java -> TimeSeriesJsonAdapter(moshi)
        else -> null
    }

}
