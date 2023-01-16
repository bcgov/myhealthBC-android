package ca.bc.gov.data.datasource.local.entity.dependent

import androidx.room.Embedded
import androidx.room.Relation

data class DependentAndListOrder(
    @Embedded val dependent: DependentEntity,
    @Relation(
        parentColumn = "hdid",
        entityColumn = "hdid"
    )
    val listOrder: DependentListOrder?
)
