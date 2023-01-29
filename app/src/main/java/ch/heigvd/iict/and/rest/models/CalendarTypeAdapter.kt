package ch.heigvd.iict.and.rest.models

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.*

class CalendarTypeAdapter : TypeAdapter<Calendar>() {
    override fun write(out: JsonWriter?, value: Calendar?) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        out?.value(dateFormat.format(value?.time))
    }

    override fun read(i: JsonReader?): Calendar {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val date = dateFormat.parse(i?.nextString())
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar
    }
}