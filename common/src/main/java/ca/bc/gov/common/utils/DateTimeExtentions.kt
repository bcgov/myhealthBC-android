package ca.bc.gov.common.utils

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

private const val PST_ZONE_ID = "America/Los_Angeles"

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
 * Parses UTC dates with any number of fraction digits, keeping millisecond precision:
 * > 2022-09-20T21:00:00Z         -> 21:00:00.000
 * > 2022-09-20T21:00:00.1Z       -> 21:00:00.100
 * > 2022-09-20T21:00:00.123456Z  -> 21:00:00.123
 */
fun String.toDateTimeZ(): Instant {
    val millisDigits = 3
    val dateWithoutZone = this.removeSuffix("Z")
    val dateTime = dateWithoutZone.substringBefore('.')
    val fraction = dateWithoutZone.substringAfter('.', missingDelimiterValue = "")
    val millis = fraction.take(millisDigits).padEnd(millisDigits, '0')
    return Instant.parse("$dateTime.${millis}Z")
}

fun Instant.toStartOfDayInstant(): Instant {
    return this.truncatedTo(ChronoUnit.DAYS)
}

/*
* Required to show the date time based on device time zone if the network response
* provides the date time in UTC+00:00
*  */
fun Instant.toLocalDateTimeInstant(): Instant? {
    return this.atZone(ZoneId.of(PST_ZONE_ID))
        ?.toLocalDateTime()?.toInstant(ZoneOffset.UTC)
}

fun Instant.toPST(): Instant {
    return this.atZone(ZoneId.of(PST_ZONE_ID))
        .toLocalDateTime().toInstant(ZoneOffset.UTC)
}

fun Instant.toLocalDate(): LocalDate =
    this.atZone(ZoneOffset.UTC).toLocalDate()

fun String.dateTimeToInstant(): Instant = Instant.parse(this)

fun String.dateToInstant(): Instant = LocalDate.parse(this).atStartOfDay(ZoneId.of(PST_ZONE_ID)).toInstant()

fun Instant.dateTimeString(pattern: String = yyyy_MMM_dd_HH_mm): String {
    return atZone(ZoneId.of(PST_ZONE_ID)).format(DateTimeFormatter.ofPattern(pattern))
}

fun Instant.dateString(pattern: String = yyyy_MMM_dd) = dateTimeString(pattern = pattern)
