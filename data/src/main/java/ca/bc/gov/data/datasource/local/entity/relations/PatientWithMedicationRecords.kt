package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.medication.MedicationRecordEntity

/**
 * @author Pinakin Kansara
 */
data class PatientWithMedicationRecords(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id",
        entity = MedicationRecordEntity::class
    )
    val medicationRecord: List<MedicationWithSummaryAndPharmacy> = emptyList()
)
