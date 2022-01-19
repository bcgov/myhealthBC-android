package ca.bc.gov.data.local.dao

import ca.bc.gov.data.local.BaseDataBaseTest
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.model.CreatePatientDto
import ca.bc.gov.data.model.toEntity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@HiltAndroidTest
class PatientDaoTest : BaseDataBaseTest() {

    lateinit var patientDao: PatientDao
    lateinit var patientAndVaccineRecordDao: PatientAndVaccineRecordDao

    override fun onCreate() {
        patientDao = db.getPatientDao()
        patientAndVaccineRecordDao = db.getPatientAndVaccineRecordDao()
    }

    @Test
    fun sampleTest() = runBlocking {

        val patient =
            PatientEntity(firstName = "Pinakin", lastName = "kansara", dateOfBirth = Instant.now())
        val id = patientDao.insertPatient(patient)

        assertTrue(id > 0)
    }

    @Test
    fun insertAndGetPatientId() = runBlocking {

        val patient = CreatePatientDto("Pinakin", "Kansara", Instant.now())

        patientDao.insertPatient(patient.toEntity())

        val id = patientDao.getPatientId(patient.firstName, patient.lastName, patient.dateOfBirth)

        assertTrue(id!! > 0)
    }

    override fun tearDown() {
    }
}
