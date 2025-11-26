package me.sofiiak.sharedplay.data

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime? {
        // Handle null values from JSON gracefully
        return if (json?.asString != null && json.asString.isNotEmpty()) {
            LocalDateTime.parse(json.asString, formatter)
        } else {
            null
        }
    }

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        // Convert the LocalDateTime object to a string using the same format
        return JsonPrimitive(src?.format(formatter))
    }
}