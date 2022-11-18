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
        )
    ]
)
data class DependentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "patient_id")
    val patientId: Long,

    @ColumnInfo(name = "hdid")
    val hdid: String,

    @ColumnInfo(name = "firstname")
    val firstname: String,

    @ColumnInfo(name = "lastname")
    val lastname: String,

    @ColumnInfo(name = "PHN")
    val PHN: String,

    @ColumnInfo(name = "gender")
    val gender: String,

    @ColumnInfo(name = "dateOfBirth")
    val dateOfBirth: Instant,
)
