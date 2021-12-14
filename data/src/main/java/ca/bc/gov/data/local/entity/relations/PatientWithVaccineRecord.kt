package ca.bc.gov.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.local.entity.PatientEntity
import ca.bc.gov.data.local.entity.VaccineRecordEntity

/**
 * @author Pinakin Kansara
 */
data class PatientWithVaccineRecord(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id"
    )
    val vaccineRecord: VaccineRecordEntity?
)
