package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecordWithForecast

/**
 * @author Pinakin Kansara
 */
data class PatientWithImmunizationRecordAndForecast(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id",
        entity = ImmunizationRecordEntity::class
    )
    val immunizationRecords: List<ImmunizationRecordWithForecast> = emptyList()
)
