package me.sofiiak.sharedplay.data

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class LocalDateTimeAdapter : JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime? {
        val str = json?.asString ?: return null
        if (str.isEmpty()) return null

        return try {
            // Try parsing date with timezone first
            OffsetDateTime.parse(str).toLocalDateTime()
        } catch (_: DateTimeParseException) {
            // Fallback to plain LocalDateTime (no offset)
            LocalDateTime.parse(str, formatter)
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