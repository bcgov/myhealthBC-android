package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.covid.test.TestResultEntity

/**
 * @author Pinakin Kansara
 */
data class PatientWithTestResultsAndRecords(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id",
        entity = TestResultEntity::class
    )
    val testResultsWithRecords: List<TestResultWithRecord> = emptyList()
)
