package me.sofiiak.sharedplay.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun formatDate(date: LocalDateTime?): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    if (date != null) {
        return date.format(formatter)
    } else {
        // todo: throw an exception?
        return ""
    }
}
