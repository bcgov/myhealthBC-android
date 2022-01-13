package ca.bc.gov.common.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun Instant.toDateTimeString(): String {
    val dateTime = LocalDateTime.ofInstant(this, ZoneOffset.UTC)
    val formatter = DateTimeFormatter.ofPattern("MMM-dd-yyyy, HH:mm")
    return formatter.format(dateTime)
}

fun Instant.toDate(): String{
    val dateTime = LocalDateTime.ofInstant(this, ZoneOffset.UTC)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return formatter.format(dateTime)
}

fun String.toDate(): Instant = LocalDate.parse(this).atStartOfDay().toInstant(ZoneOffset.UTC)

fun String.toDateTime(): Instant = LocalDateTime.parse(this).toInstant(ZoneOffset.UTC)
