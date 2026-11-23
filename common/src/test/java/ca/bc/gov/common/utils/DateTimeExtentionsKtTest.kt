package ca.bc.gov.common.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.format.DateTimeParseException
import java.util.Locale

internal class DateTimeExtentionsKtTest {

    @Test
    fun `GIVEN no fraction WHEN toDateTimeZ is called THEN whole seconds are returned`() {
        val result = "2022-09-22T07:00:00Z".toDateTimeZ()

        assertEquals("2022-09-22T07:00:00Z", result.toString())
    }

    @Test
    fun `GIVEN 1 fraction digit WHEN toDateTimeZ is called THEN it is padded to millis`() {
        val result = "2022-09-22T07:00:00.1Z".toDateTimeZ()

        assertEquals("2022-09-22T07:00:00.100Z", result.toString())
    }

    @Test
    fun `GIVEN 2 fraction digits WHEN toDateTimeZ is called THEN they are padded to millis`() {
        val result = "2022-09-22T07:00:00.12Z".toDateTimeZ()

        assertEquals("2022-09-22T07:00:00.120Z", result.toString())
    }

    @Test
    fun `GIVEN 3 fraction digits WHEN toDateTimeZ is called THEN millis are kept`() {
        val result = "2022-09-22T07:00:00.123Z".toDateTimeZ()

        assertEquals("2022-09-22T07:00:00.123Z", result.toString())
    }

    @Test
    fun `GIVEN more than 3 fraction digits WHEN toDateTimeZ is called THEN they are truncated to millis`() {
        val result4digits = "2022-09-22T07:00:00.1234Z".toDateTimeZ()

        assertEquals("2022-09-22T07:00:00.123Z", result4digits.toString())

        val result12Digits = "2022-09-22T07:00:00.123456789012Z".toDateTimeZ()

        assertEquals("2022-09-22T07:00:00.123Z", result12Digits.toString())
    }

    @Test
    fun `GIVEN fraction 999 WHEN toDateTimeZ is called THEN it is truncated without rounding up`() {
        val result = "2022-12-31T23:59:59.9999Z".toDateTimeZ()

        assertEquals("2022-12-31T23:59:59.999Z", result.toString())
    }

    @Test
    fun `GIVEN zero fraction WHEN toDateTimeZ is called THEN whole seconds are returned`() {
        val result = "2022-09-22T07:00:00.0000000Z".toDateTimeZ()

        assertEquals("2022-09-22T07:00:00Z", result.toString())
    }

    @Test
    fun `GIVEN leap day WHEN toDateTimeZ is called THEN date is parsed`() {
        val result = "2024-02-29T12:30:45.5Z".toDateTimeZ()

        assertEquals("2024-02-29T12:30:45.500Z", result.toString())
    }

    @Test(expected = DateTimeParseException::class)
    fun `GIVEN non numeric fraction WHEN toDateTimeZ is called THEN exception is thrown`() {
        "2022-09-22T07:00:00.abcZ".toDateTimeZ()
    }

    @Test(expected = DateTimeParseException::class)
    fun `GIVEN invalid date WHEN toDateTimeZ is called THEN exception is thrown`() {
        "2022-02-30T07:00:00Z".toDateTimeZ()
    }

    @Test(expected = DateTimeParseException::class)
    fun `GIVEN date without time WHEN toDateTimeZ is called THEN exception is thrown`() {
        "2022-09-22".toDateTimeZ()
    }

    @Test(expected = DateTimeParseException::class)
    fun `GIVEN empty string WHEN toDateTimeZ is called THEN exception is thrown`() {
        "".toDateTimeZ()
    }
}
