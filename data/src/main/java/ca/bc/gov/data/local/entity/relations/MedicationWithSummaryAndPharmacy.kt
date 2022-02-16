package ca.bc.gov.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.local.entity.DispensingPharmacyEntity
import ca.bc.gov.data.local.entity.MedicationRecordEntity
import ca.bc.gov.data.local.entity.MedicationSummaryEntity

/**
 * @author Pinakin Kansara
 */
data class MedicationWithSummaryAndPharmacy(
    @Embedded
    val medicationRecord: MedicationRecordEntity,
    @Relation(
        entity = MedicationSummaryEntity::class,
        parentColumn = "id",
        entityColumn = "medication_record_id"
    )
    val medicationSummary: MedicationSummaryEntity,
    @Relation(
        entity = DispensingPharmacyEntity::class,
        parentColumn = "id",
        entityColumn = "medication_record_id"
    )
    val dispensingPharmacy: DispensingPharmacyEntity
)
