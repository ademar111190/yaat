package ademar.template.network.adapter

import ademar.template.network.payload.Series
import ademar.template.network.payload.TimeSeries
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class TimeSeriesJsonAdapter(
    private val moshi: Moshi,
) : JsonAdapter<TimeSeries>() {

    override fun toJson(writer: JsonWriter, timeSeries: TimeSeries?) {
        if (timeSeries == null) return
        val seriesMap = timeSeries.series ?: return
        val seriesAdapter = moshi.adapter(Series::class.java)
        writer.beginObject()
        for ((date, series) in seriesMap.entries) {
            if (date == null) continue
            writer.beginObject()
            writer.name(date)
            seriesAdapter.toJson(writer, series)
            writer.endObject()
        }
        writer.endObject()
    }

    override fun fromJson(reader: JsonReader): TimeSeries? {
        if (!reader.hasNext() || reader.peek() != JsonReader.Token.BEGIN_OBJECT) return null
        val seriesAdapter = moshi.adapter(Series::class.java)
        val series = mutableMapOf<String, Series?>()
        reader.beginObject()
        while (reader.peek() != JsonReader.Token.END_OBJECT) {
            series[reader.nextName()] = seriesAdapter.fromJson(reader)
        }
        reader.endObject()
        return TimeSeries(series.toMap())
    }

}
