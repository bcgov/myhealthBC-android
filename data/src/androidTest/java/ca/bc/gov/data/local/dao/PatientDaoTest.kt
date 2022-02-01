package ca.bc.gov.data.local.dao

import ca.bc.gov.data.local.BaseDataBaseTest
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

    override fun onCreate() {
        patientDao = db.getPatientDao()
        vaccineRecordDao = db.getVaccineRecordDao()
        testResultDao = db.getTestResultDao()
    }

    @Test
    fun insertPatient() = runBlocking {
        //Given
        val patient = getPatient1()

        //When
        val result = patientDao.insertPatient(patient)

        //Then
        Assert.assertEquals(1, result)
    }


    @Test
    fun insertDuplicatePatient() = runBlocking {
        //Given
        val patient = getPatient1()

        //When
        patientDao.insertPatient(patient)
        val result = patientDao.insertPatient(patient)

        //Then
        Assert.assertNotEquals(1, result)
    }

    @Test
    fun checkPatientId() = runBlocking {
        //Given
        val patient = getPatient1()

        //When
        patientDao.insertPatient(patient)

        //Then
        val insertedPatientId =
            patientDao.getPatientId(patient.firstName, patient.lastName, patient.dateOfBirth)
        Assert.assertEquals(patient.id, insertedPatientId)
    }

    @Test
    fun checkPatient() = runBlocking {
        //Given
        val patient = getPatient1()

        //When
        patientDao.insertPatient(patient)

        //Then
        val insertedPatient =
            patientDao.getPatient(patient.id)
        Assert.assertEquals(patient, insertedPatient)
    }

    @Test
    fun checkPatientWithRecordCountFlow() = runBlocking {
        //Given
        val patient1 = getPatient1()
        val patient2 = getPatient2()
        val vaccineRecord1 = getVaccineRecord1()
        val vaccineRecord2 = getVaccineRecord2()
        val testResult1 = getTestResult1()
        val testResult2 = getTestResult2()
        val testResult3 = getTestResult3()

        //When
        patientDao.insertPatient(patient1)
        patientDao.insertPatient(patient2)
        vaccineRecordDao.insertVaccineRecord(vaccineRecord1)
        vaccineRecordDao.insertVaccineRecord(vaccineRecord2)
        testResultDao.insertTestResult(testResult1)
        testResultDao.insertTestResult(testResult2)
        testResultDao.insertTestResult(testResult3)

        //Then
        val result = patientDao.getPatientWithRecordCountFlow().first()
        Assert.assertTrue(result.contains(getPatientWithHealthRecordCount1(patient1)))
        Assert.assertTrue(result.contains(getPatientWithHealthRecordCount2(patient2)))
    }

    override fun tearDown() {
    }
}
