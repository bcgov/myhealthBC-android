package ca.bc.gov.data.datasource.local.entity.covid

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @author Pinakin Kansara
 */
data class CovidOrderWithCovidTests(
    @Embedded
    val covidOrder: CovidOrderEntity,
    @Relation(
        entity = CovidTestEntity::class,
        parentColumn = "id",
        entityColumn = "covid_order_id"
    )
    val covidTests: List<CovidTestEntity>
)
