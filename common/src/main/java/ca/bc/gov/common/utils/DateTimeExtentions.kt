package ca.bc.gov.common.utils

import ca.bc.gov.common.exceptions.InvalidResponseException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

const val yyyy_MMM_dd_HH_mm = "yyyy-MMM-dd, hh:mm a"
const val yyyy_MMM_dd = "yyyy-MMM-dd"
const val yyyy_MM_dd = "yyyy-MM-dd"
const val eee_dd_mmm_yyyy_hh_mm_ss_z = "EEE, dd MMM yyyy HH:mm:ss XXXX"

private const val full_date_time_plus_time = "yyyy-MM-dd'T'HH:mm:ssXXX"
private const val yyyy_MMM_dd_HH_mm_sss_long = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private const val yyyy_MMM_dd_HH_mm_short = "yyyy-MM-dd'T'HH:mm:ss'Z'"

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

fun String.toOffsetDateTime(): Instant =
    this.toDateTime(full_date_time_plus_time).toLocalDateTimeInstant()
        ?: throw InvalidResponseException()

fun String.toDate(): Instant = LocalDate.parse(this).atStartOfDay().toInstant(ZoneOffset.UTC)

fun String.toDateTime(formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME): Instant =
    LocalDateTime.parse(this, formatter).toInstant(ZoneOffset.UTC)

fun String.toPstFromIsoZoned(): Instant {
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    val dateWithoutZ = this.replace("Z", "+00:00")

    val dateStr = if (dateWithoutZ.endsWith("+00:00")) {
        dateWithoutZ
    } else {
        "$dateWithoutZ+00:00"
    }

    return LocalDateTime.parse(dateStr, formatter)
        .toInstant(ZoneOffset.UTC)
        .toLocalDateTimeInstant() ?: Instant.now()
}

fun String.toDateTime(datePattern: String): Instant =
    this.toDateTime(DateTimeFormatter.ofPattern(datePattern))

/**
 * Parse dates in the following formats:
 * > 2022-09-20T21:00:00.123Z
 * > 2022-09-20T21:00:00Z
 * This method limits input length so it won't break if a longer dates are received:
 * Eg.: 1234Z, 12345Z, 123456Z an so on
 */
fun String.toDateTimeZ(): Instant {
    val patternLengthMin = 20
    val patternLengthMax = 24
    var datePattern = yyyy_MMM_dd_HH_mm_sss_long

    val dateStr = if (this.length > patternLengthMax) {
        this.substring(0, patternLengthMax - 1).plus("Z")
    } else {
        if (this.length <= patternLengthMin) {
            datePattern = yyyy_MMM_dd_HH_mm_short
        }
        this
    }

    return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(datePattern))
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
    return this.atZone(ZoneId.of("America/Los_Angeles"))
        ?.toLocalDateTime()?.toInstant(ZoneOffset.UTC)
}

fun Instant.toLocalDate(): LocalDate =
    this.atZone(ZoneOffset.UTC).toLocalDate()
