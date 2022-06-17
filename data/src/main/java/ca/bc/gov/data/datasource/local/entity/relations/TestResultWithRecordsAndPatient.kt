package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity

/**
 * @author Pinakin Kansara
 */
data class TestResultWithRecordsAndPatient(
    @Embedded
    val testResultWithRecord: TestResultWithRecord,
    @Relation(
        parentColumn = "patient_id",
        entityColumn = "id",
        entity = PatientEntity::class
    )
    val patient: PatientEntity
)
