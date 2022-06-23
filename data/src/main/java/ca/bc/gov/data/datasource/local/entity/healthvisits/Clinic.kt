package ca.bc.gov.data.datasource.local.entity.healthvisits

import androidx.room.ColumnInfo

/*
* Created by amit_metri on 21,June,2022
*/
data class Clinic(
    @ColumnInfo(name = "name")
    val name: String? = null,
)
