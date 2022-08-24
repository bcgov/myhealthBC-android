package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.healthvisits.HealthVisitEntity

/*
* Created by amit_metri on 21,June,2022
*/
data class PatientWithHealthVisits(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id"
    )
    val healthVisits: List<HealthVisitEntity> = emptyList()
)
