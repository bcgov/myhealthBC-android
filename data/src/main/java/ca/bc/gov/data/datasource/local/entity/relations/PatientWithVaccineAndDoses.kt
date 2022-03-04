package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.covid.vaccine.VaccineRecordEntity

/**
 * @author Pinakin Kansara
 */
data class PatientWithVaccineAndDoses(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id",
        entity = VaccineRecordEntity::class
    )
    val vaccineRecordWithDose: VaccineRecordWithDose?
)
