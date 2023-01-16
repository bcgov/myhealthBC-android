package ca.bc.gov.common.model.dependents

import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

internal class DependentDtoTest {

    private val birthDate = Instant.parse("2000-01-01T00:00:00Z")

    @Test
    fun `GIVEN today is one day before 12 years WHEN isDependentAgedOut is called THEN result is false`() {
        val today = LocalDate.of(2011, 12, 31)

        val dependentDto = instantiateDependent(birthDate)
        val result = dependentDto.isDependentAgedOut(today)

        Assert.assertEquals(false, result)
    }

    @Test
    fun `GIVEN today 12 years birthday WHEN isDependentAgedOut is called THEN result is true`() {
        val today = LocalDate.of(2012, 1, 1)

        val dependentDto = instantiateDependent(birthDate)
        val result = dependentDto.isDependentAgedOut(today)

        Assert.assertEquals(true, result)
    }

    @Test
    fun `GIVEN today is one day after 12 years WHEN isDependentAgedOut is called THEN result is true`() {
        val today = LocalDate.of(2012, 1, 2)

        val dependentDto = instantiateDependent(birthDate)
        val result = dependentDto.isDependentAgedOut(today)

        Assert.assertEquals(true, result)
    }

    private fun instantiateDependent(birthDate: Instant) = DependentDto(
        hdid = "",
        firstname = "",
        lastname = "",
        phn = "",
        dateOfBirth = birthDate,
        gender = "",
        ownerId = "",
        delegateId = "",
        reasonCode = 0L,
        version = 0L,
    )
}
