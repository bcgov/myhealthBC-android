package ca.bc.gov.data.datasource.local.entity.labtest

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity

/**
 * @author Pinakin Kansara
 */
data class LabOrderWithLabTestsAndPatient(
    @Embedded
    val labOrderWithLabTests: LabOrderWithLabTests,
    @Relation(
        parentColumn = "patient_id",
        entityColumn = "id",
        entity = PatientEntity::class
    )
    val patient: PatientEntity
)
