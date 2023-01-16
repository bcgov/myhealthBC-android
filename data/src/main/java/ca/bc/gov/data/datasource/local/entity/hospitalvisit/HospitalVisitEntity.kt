package ca.bc.gov.data.datasource.local.entity.hospitalvisit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import java.time.Instant

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

    @ColumnInfo(name = "health_service")
    val healthService: String,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "provider")
    val provider: String,

    @ColumnInfo(name = "visit_type")
    val visitType: String,

    @ColumnInfo(name = "visit_date")
    val visitDate: Instant,

    @ColumnInfo(name = "discharge_date")
    val dischargeDate: Instant?,
)
