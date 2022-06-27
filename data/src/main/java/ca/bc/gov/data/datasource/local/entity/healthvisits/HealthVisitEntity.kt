package ca.bc.gov.data.datasource.local.entity.healthvisits

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import java.time.Instant

/*
* Created by amit_metri on 21,June,2022
*/
@Entity(
    tableName = "health_visits",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class HealthVisitEntity(
    // Defined own primary key("health_visit_id") because "id" is set as nullable from the API specs
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "health_visit_id")
    val healthVisitId: Long = 0,
    @ColumnInfo(name = "patient_id")
    val patientId: Long,
    @ColumnInfo(name = "id")
    val id: String? = null,
    @ColumnInfo(name = "encounter_date")
    val encounterDate: Instant,
    @ColumnInfo(name = "specialty_description")
    val specialtyDescription: String? = null,
    @ColumnInfo(name = "practitioner_name")
    val practitionerName: String? = null,
    @Embedded val clinic: Clinic? = null,
    @ColumnInfo(name = "data_source")
    val dataSource: DataSource = DataSource.BCSC
)
