package ca.bc.gov.common.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

const val yyyy_MMM_dd_HH_mm = "yyyy-MMM-dd, hh:mm a"
const val yyyy_MMM_dd_HH_mm_sss = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val yyyy_MMM_dd = "yyyy-MMM-dd"
const val yyyy_MM_dd = "yyyy-MM-dd"
const val eee_dd_mmm_yyyy_hh_mm_ss_z = "EEE, dd MMM yyyy HH:mm:ss XXXX"

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

/**
 * Parse dates in the following format: 2022-09-20T21:00:00.123Z
 * This method limits input length so it won't break if a longer date is received:
 * Eg.: 1234Z, 12345Z, 123456Z an so on
 */
fun String.toDateTimeZ(): Instant {
    val patternLength = 24
    val dateStr = if (this.length > patternLength) {
        this.substring(0, patternLength - 1).plus("Z")
    } else {
        this
    }

    return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(yyyy_MMM_dd_HH_mm_sss))
        .toInstant(ZoneOffset.UTC)
}

fun Instant.toStartOfDayInstant(): Instant {
    return this.truncatedTo(ChronoUnit.DAYS)
}

/*
* Required to show the date time based on device time zone if the network response
* provides the date time in UTC+00:00
*  */
fun Instant.toLocalDateTimeInstant(): Instant? {
    return this.atZone(ZoneId.systemDefault())
        ?.toLocalDateTime()?.toInstant(ZoneOffset.UTC)
}
