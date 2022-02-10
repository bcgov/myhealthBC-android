package ca.bc.gov.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.local.entity.PatientEntity

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
