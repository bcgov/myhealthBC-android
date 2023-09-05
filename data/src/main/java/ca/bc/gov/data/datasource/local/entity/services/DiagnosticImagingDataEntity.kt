package ca.bc.gov.data.datasource.local.entity.services

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import java.time.Instant

@Entity(
    tableName = "diagnostic_imaging",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = androidx.room.ForeignKey.CASCADE,
            onUpdate = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class DiagnosticImagingDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "patient_id")
    val patientId: Long,
    @ColumnInfo(name = "diagnostic_imaging_id")
    val diagnosticImagingId: String?,
    @ColumnInfo(name = "exam_date")
    val examDate: Instant?,
    @ColumnInfo(name = "file_id")
    val fileId: String?,
    @ColumnInfo(name = "exam_status")
    val examStatus: String,
    @ColumnInfo(name = "health_authority")
    val healthAuthority: String?,
    val organization: String?,
    val modality: String?,
    @ColumnInfo(name = "body_part")
    val bodyPart: String?,
    @ColumnInfo(name = "procedure_description")
    val procedureDescription: String?,
    @ColumnInfo(name = "updated", defaultValue = "false")
    val isUpdated: Boolean
)
