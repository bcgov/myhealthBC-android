package ca.bc.gov.data.datasource.local.entity.immunization

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @author Pinakin Kansara
 */
data class ImmunizationRecordWithForecast(
    @Embedded
    val immunizationRecord: ImmunizationRecordEntity,
    @Relation(
        entity = ImmunizationForecastEntity::class,
        parentColumn = "id",
        entityColumn = "immunization_record_id"
    )
    val immunizationForecast: ImmunizationForecastEntity?
)
