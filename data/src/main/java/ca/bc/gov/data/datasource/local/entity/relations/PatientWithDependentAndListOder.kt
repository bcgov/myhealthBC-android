package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.dependent.DependentEntity

data class PatientWithDependentAndListOder(
    @Embedded
    val patient: PatientEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "guardian_id"
    )
    val dependentAndListOrder: List<DependentEntity> = emptyList()
)
