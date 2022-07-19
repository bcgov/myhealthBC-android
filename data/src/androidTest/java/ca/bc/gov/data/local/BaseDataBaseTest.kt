package ca.bc.gov.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import ca.bc.gov.common.model.AuthenticationStatus
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.model.ImmunizationStatus
import ca.bc.gov.data.datasource.local.MyHealthDataBase
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.comment.CommentEntity
import ca.bc.gov.data.datasource.local.entity.covid.test.TestResultEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineRecordEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationRecordEntity
import ca.bc.gov.data.datasource.local.entity.relations.PatientWithHealthRecordCount
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
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

    protected fun getPatient(
        id: Long = 1,
        fullName: String = "Jhon Smith",
        dateOfBirth: Instant = Instant.now(),
        phn: String = "986767878",
        timeStamp: Instant = Instant.now(),
        patientOrder: Long = Long.MAX_VALUE,
        authenticationStatus: AuthenticationStatus = AuthenticationStatus.AUTHENTICATED
    ): PatientEntity {
        val patient = mockk<PatientEntity>()
        every { patient.id } returns id
        every { patient.fullName } returns fullName
        every { patient.dateOfBirth } returns dateOfBirth
        every { patient.phn } returns phn
        every { patient.timeStamp } returns timeStamp
        every { patient.patientOrder } returns patientOrder
        every { patient.authenticationStatus } returns authenticationStatus
        return patient
    }

    protected fun getLabOrder(
        id: Long = 1,
        patientId: Long = 1,
        collectionDateTime: Instant = Instant.now(),
        reportAvailable: Boolean = false,
        commonName: String? = "Test Name",
        orderingProvider: String? = "Ordering Provider",
        reportId: String? = "Report ID",
        reportingSource: String? = "Reporting Source",
        testStatus: String? = "Test Status"
    ): LabOrderEntity {

        val labOrder = mockk<LabOrderEntity>()
        every { labOrder.id } returns id
        every { labOrder.patientId } returns patientId
        every { labOrder.collectionDateTime } returns collectionDateTime
        every { labOrder.reportAvailable } returns reportAvailable
        every { labOrder.commonName } returns commonName
        every { labOrder.orderingProvider } returns orderingProvider
        every { labOrder.reportId } returns reportId
        every { labOrder.reportingSource } returns reportingSource
        every { labOrder.testStatus } returns testStatus
        return labOrder
    }

    protected fun getComment(
        id: String = UUID.randomUUID().toString(),
        userProfileId: String = UUID.randomUUID().toString(),
        text: String = "Sample Comment",
        entryTypeCode: String = "Med",
        parentEntryId: String = UUID.randomUUID().toString(),
        version: Int = 0,
        createdDateTime: Instant = Instant.now(),
        createdBy: String = "Created By Pinakin",
        updateDateTime: Instant = Instant.now(),
        updatedBy: String = "Updated By Pinakin"
    ): CommentEntity {
        val comment = mockk<CommentEntity>()
        every { comment.id } returns id
        every { comment.userProfileId } returns userProfileId
        every { comment.text } returns text
        every { comment.entryTypeCode } returns entryTypeCode
        every { comment.parentEntryId } returns parentEntryId
        every { comment.version } returns version
        every { comment.createdDateTime } returns createdDateTime
        every { comment.createdBy } returns createdBy
        every { comment.updatedDateTime } returns updateDateTime
        every { comment.updatedBy } returns updatedBy
        return comment
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
        prescriptionIdentifier = "Practitioner",
        prescriptionStatus = "Status",
        practitionerSurname = "Practitioner Surname",
        dispenseDate = Instant.now(),
        directions = "Directions",
        dateEntered = Instant.now(),
        dataSource = DataSource.BCSC
    )

    protected fun getPatientWithHealthRecordCount1(patientEntity: PatientEntity) =
        PatientWithHealthRecordCount(
            patientEntity = patientEntity,
            vaccineRecordCount = 2,
            testRecordCount = 2,
            labTestCount = 0,
            covidTestCount = 0,
            medicationRecordCount = 1
        )

    protected fun getPatientWithHealthRecordCount2(patientEntity: PatientEntity) =
        PatientWithHealthRecordCount(
            patientEntity = patientEntity,
            vaccineRecordCount = 0,
            testRecordCount = 1,
            labTestCount = 0,
            covidTestCount = 0,
            medicationRecordCount = 0
        )
}
