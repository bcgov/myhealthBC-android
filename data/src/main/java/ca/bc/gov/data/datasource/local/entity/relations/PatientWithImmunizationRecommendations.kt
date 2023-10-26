package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.immunization.ImmunizationRecommendationEntity

data class PatientWithImmunizationRecommendations(
    @Embedded
    val patient: PatientEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id"
    )
    val recommendations: List<ImmunizationRecommendationEntity> = emptyList()
)
