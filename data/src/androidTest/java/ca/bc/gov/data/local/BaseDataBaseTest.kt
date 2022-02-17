package ca.bc.gov.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.model.ImmunizationStatus
import ca.bc.gov.data.local.entity.MedicationRecordEntity
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.TestResultEntity
import ca.bc.gov.data.local.entity.VaccineRecordEntity
import ca.bc.gov.data.local.entity.relations.PatientWithHealthRecordCount
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Named

/**
 * @author Pinakin Kansara
 */
@HiltAndroidTest
abstract class BaseDataBaseTest {

    @get:Rule
    val hiltRule by lazy { HiltAndroidRule(this) }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var db: MyHealthDataBase

    abstract fun onCreate()

    abstract fun tearDown()

    @Before
    fun setUp() {
        hiltRule.inject()
        onCreate()
    }

    @After
    fun close() {
        db.close()
        tearDown()
    }

    protected fun getPatient1() =
        PatientEntity(
            id = 1,
            fullName = "Random random",
            dateOfBirth = Instant.now(),
            phn = "12333456",
            timeStamp = Instant.now(),
            patientOrder = Long.MAX_VALUE
        )

    protected fun getPatient2() =
        PatientEntity(
            id = 2,
            fullName = "Rashmi Bambhania",
            dateOfBirth = Instant.now(),
            phn = "12333456",
            timeStamp = Instant.now(),
            patientOrder = Long.MAX_VALUE
        )

    protected fun getVaccineRecord1() = VaccineRecordEntity(
        id = 1,
        patientId = 1,
        qrIssueDate = Instant.now().minus(28, ChronoUnit.DAYS),
        status = ImmunizationStatus.PARTIALLY_IMMUNIZED,
        shcUri = "shcUri1",
        federalPass = "federalPass1",
        dataSource = DataSource.QR_CODE
    )
    protected fun getVaccineRecord2() = VaccineRecordEntity(
        id = 2,
        patientId = 1,
        qrIssueDate = Instant.now(),
        status = ImmunizationStatus.FULLY_IMMUNIZED,
        shcUri = "shcUri2",
        federalPass = "federalPass2",
        dataSource = DataSource.QR_CODE
    )

    protected fun getTestResult1() = TestResultEntity(
        id = 1,
        patientId = 1,
        collectionDate = Instant.now().minus(20, ChronoUnit.DAYS)
    )

    protected fun getTestResult2() = TestResultEntity(
        id = 2,
        patientId = 1,
        collectionDate = Instant.now()
    )

    protected fun getTestResult3() = TestResultEntity(
        id = 3,
        patientId = 2,
        collectionDate = Instant.now()
    )

    protected fun getMedicationRecord() = MedicationRecordEntity(
        id = 0,
        patientId = 1,
        practitionerIdentifier = "Practitioner",
        prescriptionStatus = "Status",
        practitionerSurname = "Practitioner Surname",
        dispenseDate = Instant.now(),
        directions = "Directions",
        dateEntered = Instant.now(),
        dataSource = DataSource.BCSC
    )

    protected fun getPatientWithHealthRecordCount1(patientEntity: PatientEntity) = PatientWithHealthRecordCount(
        patientEntity = patientEntity,
        vaccineRecordCount = 2,
        testRecordCount = 2,
        medicationRecordCount = 1
    )

    protected fun getPatientWithHealthRecordCount2(patientEntity: PatientEntity) = PatientWithHealthRecordCount(
        patientEntity = patientEntity,
        vaccineRecordCount = 0,
        testRecordCount = 1,
        medicationRecordCount = 0
    )
}
