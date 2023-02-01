package ca.bc.gov.common.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

internal class DateTimeExtentionsKtTest {

    @Test
    fun `GIVEN offsetDateTime WHEN toOffsetDateTime is called THEN PST should be returned`() {
        val dateStr = "2022-10-21T00:00:00+00:00"
        val expected = "2022-Oct-20, 05:00 pm"

        val instant = dateStr.toOffsetDateTime()
        val result = instant.toDateTimeString()
        println(result)
        assertEquals(expected, result)
    }

    @Test
    fun `GIVEN longest date String WHEN formatZ is called THEN no exception should be thrown`() {
        val dateStr = "2022-09-22T07:00:00.123456Z"
        try {
            val result = dateStr.toDateTimeZ()
            println(result)
            assert(true)
        } catch (e: Exception) {
            fail(e.message)
        }
    }

    @Test
    fun `GIVEN long date String WHEN formatZ is called THEN no exception should be thrown`() {
        val dateStr = "2022-09-22T07:00:00.123Z"
        try {
            val result = dateStr.toDateTimeZ()
            println(result)
            assert(true)
        } catch (e: Exception) {
            fail(e.message)
        }
    }

    @Test
    fun `GIVEN regular date String WHEN formatZ is called THEN no exception should be thrown`() {
        val dateStr = "2022-09-22T07:00:00.12Z"
        try {
            val result = dateStr.toDateTimeZ()
            println(result)
            assert(true)
        } catch (e: Exception) {
            fail(e.message)
        }
    }

    @Test
    fun `GIVEN short date String WHEN formatZ is called THEN no exception should be thrown`() {
        val dateStr = "2022-09-22T07:00:00Z"
        try {
            val result = dateStr.toDateTimeZ()
            println(result)
            assert(true)
        } catch (e: Exception) {
            fail(e.message)
        }
    }
}
