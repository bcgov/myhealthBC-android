package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.specialauthority.SpecialAuthorityEntity

/*
* Created by amit_metri on 29,June,2022
*/
data class PatientWithSpecialAuthorities(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id"
    )
    val specialAuthorities: List<SpecialAuthorityEntity> = emptyList()
)
