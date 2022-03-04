package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.medication.DispensingPharmacyEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationRecordEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationSummaryEntity

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
