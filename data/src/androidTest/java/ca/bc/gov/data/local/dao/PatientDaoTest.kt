package ca.bc.gov.data.local.dao

import ca.bc.gov.data.local.BaseDataBaseTest
import ca.bc.gov.data.local.entity.PatientEntity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

/**
 * @author Pinakin Kansara
 */
@HiltAndroidTest
class PatientDaoTest : BaseDataBaseTest() {

    private lateinit var patientDao: PatientDao
    private lateinit var vaccineRecordDao: VaccineRecordDao
    private lateinit var testResultDao: TestResultDao
    private lateinit var medicationRecordDao: MedicationRecordDao

    override fun onCreate() {
        patientDao = db.getPatientDao()
        vaccineRecordDao = db.getVaccineRecordDao()
        testResultDao = db.getTestResultDao()
        medicationRecordDao = db.getMedicationRecordDao()
    }

    @Test
    fun insertPatient() = runBlocking {
        // Given
        val patient = getPatient1()

        // When
        val result = patientDao.insert(patient)

        // Then
        Assert.assertTrue(result > 0)
    }

    @Test
    fun insertDuplicatePatient() = runBlocking {
        // Given
        val patient = getPatient1()

        // When
        patientDao.insert(patient)
        val result = patientDao.insert(patient)

        // Then
        Assert.assertEquals(-1, result)
    }

    @Test
    fun checkPatientId() = runBlocking {
        // Given
        val patient = getPatient1()

        // When
        patientDao.insert(patient)

        // Then
        val insertedPatientId =
            patientDao.getPatientId(patient.fullName, patient.dateOfBirth)
        Assert.assertEquals(patient.id, insertedPatientId)
    }

    @Test
    fun checkPatient() = runBlocking {
        // Given
        val patient = getPatient1()

        // When
        patientDao.insert(patient)

        // Then
        val insertedPatient =
            patientDao.getPatient(patient.id)
        assertPatientData(patient, insertedPatient)
    }

    @Test
    fun checkPatientWithRecordCountFlow() = runBlocking {
        // Given
        val patient1 = getPatient1()
        val patient2 = getPatient2()
        val vaccineRecord1 = getVaccineRecord1()
        val vaccineRecord2 = getVaccineRecord2()
        val testResult1 = getTestResult1()
        val testResult2 = getTestResult2()
        val testResult3 = getTestResult3()
        val medicationRecord = getMedicationRecord()

        // When
        patientDao.insert(patient1)
        patientDao.insert(patient2)
        vaccineRecordDao.insert(vaccineRecord1)
        vaccineRecordDao.insert(vaccineRecord2)
        testResultDao.insert(testResult1)
        testResultDao.insert(testResult2)
        testResultDao.insert(testResult3)
        medicationRecordDao.insert(medicationRecord)

        // Then
        val result = patientDao.getPatientWithHealthRecordCountFlow().first()
        Assert.assertTrue(result.contains(getPatientWithHealthRecordCount1(patient1)))
        Assert.assertTrue(result.contains(getPatientWithHealthRecordCount2(patient2)))
    }

    private fun assertPatientData(expectedPatient: PatientEntity, insertedPatient: PatientEntity) {
        Assert.assertEquals(expectedPatient, insertedPatient)
        Assert.assertEquals(expectedPatient.id, insertedPatient.id)
        Assert.assertEquals(expectedPatient.fullName, insertedPatient.fullName)
        Assert.assertEquals(expectedPatient.dateOfBirth, insertedPatient.dateOfBirth)
        Assert.assertEquals(expectedPatient.patientOrder, insertedPatient.patientOrder)
        Assert.assertEquals(expectedPatient.phn, insertedPatient.phn)
        Assert.assertEquals(expectedPatient.timeStamp, insertedPatient.timeStamp)
    }

    override fun tearDown() {
    }
}
