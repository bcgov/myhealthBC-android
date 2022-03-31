package ca.bc.gov.data.datasource.local.entity.labtest

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @author Pinakin Kansara
 */
data class LabOrderWithLabTests(
    @Embedded
    val labOrder: LabOrderEntity,
    @Relation(
        entity = LabTestEntity::class,
        parentColumn = "id",
        entityColumn = "lab_order_id"
    )
    val labTests: List<LabTestEntity>
)
