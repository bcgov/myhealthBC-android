package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderEntity
import ca.bc.gov.data.datasource.local.entity.labtest.LabOrderWithLabTests

data class PatientWithLabOrdersAndLabTests(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id",
        entity = LabOrderEntity::class
    )
    val labOrdersWithLabTests: List<LabOrderWithLabTests> = emptyList()
)
