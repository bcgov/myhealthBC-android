package ca.bc.gov.data.datasource.local.entity.dependent

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import java.time.Instant

@Entity(
    tableName = "dependent",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["guardian_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DependentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "patient_id")
    val patientId: Long,

    @ColumnInfo(name = "guardian_id")
    val guardianId: Long,

    @ColumnInfo(name = "hdid")
    val hdid: String,

    @ColumnInfo(name = "firstname")
    val firstname: String,

    @ColumnInfo(name = "lastname")
    val lastname: String,

    @ColumnInfo(name = "PHN")
    val phn: String,

    @ColumnInfo(name = "gender")
    val gender: String,

    @ColumnInfo(name = "dateOfBirth")
    val dateOfBirth: Instant,

    @ColumnInfo(name = "ownerId")
    val ownerId: String,

    @ColumnInfo(name = "delegateId")
    val delegateId: String,

    @ColumnInfo(name = "reasonCode")
    val reasonCode: Long,

    @ColumnInfo(name = "totalDelegateCount", defaultValue = "0")
    val totalDelegateCount: Long,

    @ColumnInfo(name = "version")
    val version: Long,

    @ColumnInfo(name = "is_cache_valid")
    val isCacheValid: Boolean,
)
