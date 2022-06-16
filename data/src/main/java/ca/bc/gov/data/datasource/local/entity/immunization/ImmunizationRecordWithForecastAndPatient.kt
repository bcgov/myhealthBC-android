package ca.bc.gov.data.datasource.local.entity.immunization

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity

/**
 * @author Pinakin Kansara
 */
data class ImmunizationRecordWithForecastAndPatient(
    @Embedded
    val immunizationRecordWithForecast: ImmunizationRecordWithForecast,
    @Relation(
        parentColumn = "patient_id",
        entityColumn = "id",
        entity = PatientEntity::class
    )
    val patient: PatientEntity
)
