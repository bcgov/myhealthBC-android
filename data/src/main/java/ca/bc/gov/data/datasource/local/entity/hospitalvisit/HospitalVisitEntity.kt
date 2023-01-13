package ca.bc.gov.data.datasource.local.entity.hospitalvisit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.data.datasource.local.entity.PatientEntity

@Entity(
    tableName = "hospital_visit",
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
data class HospitalVisitEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "hospital_visit_id")
    val healthVisitId: Long = 0,

    @ColumnInfo(name = "patient_id")
    val patientId: Long,
)
