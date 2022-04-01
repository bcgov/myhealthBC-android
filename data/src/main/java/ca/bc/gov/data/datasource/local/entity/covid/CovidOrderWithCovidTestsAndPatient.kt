package ca.bc.gov.data.datasource.local.entity.covid

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity

/**
 * @author Pinakin Kansara
 */
data class CovidOrderWithCovidTestsAndPatient(
    @Embedded
    val covidOrderWithCovidTests: CovidOrderWithCovidTests,
    @Relation(
        parentColumn = "patient_id",
        entityColumn = "id",
        entity = PatientEntity::class
    )
    val patient: PatientEntity
)
