package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderEntity
import ca.bc.gov.data.datasource.local.entity.covid.CovidOrderWithCovidTests

/**
 * @author Pinakin Kansara
 */
data class PatientWithCovidOrderAndCovidTest(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id",
        entity = CovidOrderEntity::class
    )
    val covidOrderWithTests: List<CovidOrderWithCovidTests> = emptyList()
)
