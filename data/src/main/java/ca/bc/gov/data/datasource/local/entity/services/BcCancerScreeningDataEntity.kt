package ca.bc.gov.data.datasource.local.entity.services

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import java.time.Instant

/**
 * @author pinakin.kansara
 * Created 2024-01-18 at 1:26 p.m.
 */
@Entity(
    tableName = "bc_cancer_screening",
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
data class BcCancerScreeningDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "patient_id")
    val patientId: Long,
    @ColumnInfo(name = "bc_cancer_screening_id")
    val bcCancerScreeningId: String?,
    @ColumnInfo(name = "result_date")
    val resultDateTime: Instant?,
    @ColumnInfo("exam_date")
    val eventDateTime: Instant?,
    @ColumnInfo("file_id")
    val fileId: String?,
    @ColumnInfo("program_name")
    val programName: String?,
    @ColumnInfo("event_type")
    val eventType: String?
)
