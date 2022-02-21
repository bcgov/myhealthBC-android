package ca.bc.gov.common.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

const val yyyy_MMM_dd_HH_mm = "yyyy-MMM-dd, HH:mm"
const val yyyy_MMM_dd = "yyyy-MMM-dd"
const val yyyy_MM_dd = "yyyy-MM-dd"

fun Instant.toDateTimeString(dateFormat: String = yyyy_MMM_dd_HH_mm): String {
    val dateTime = LocalDateTime.ofInstant(this, ZoneOffset.UTC)
    val formatter = DateTimeFormatter.ofPattern(dateFormat)
    val formattedString = formatter.format(dateTime)
    return formattedString.replace(".", "")
}

fun Instant.toDate(dateFormat: String = yyyy_MMM_dd): String {
    val dateTime = LocalDateTime.ofInstant(this, ZoneOffset.UTC)
    val formatter = DateTimeFormatter.ofPattern(dateFormat)
    val formattedString = formatter.format(dateTime)
    return formattedString.replace(".", "")
}

fun String.toDate(): Instant = LocalDate.parse(this).atStartOfDay().toInstant(ZoneOffset.UTC)

fun String.toDateTime(formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME): Instant =
    LocalDateTime.parse(this, formatter).toInstant(ZoneOffset.UTC)

fun String.formatInPattern(
    formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME,
    pattern: String = yyyy_MM_dd
): String {
    return this.toDateTime(formatter).toDateTimeString(pattern)
}
