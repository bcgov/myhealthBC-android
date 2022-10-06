package ca.bc.gov.data.datasource.local.entity.immunization

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import java.time.Instant

@Entity(
    tableName = "immunization_recommendation",
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
data class ImmunizationRecommendationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "patient_id")
    val patientId: Long,

    @ColumnInfo(name = "recommendation_set_id")
    val recommendationSetId: String?,

    @ColumnInfo(name = "immunization_name")
    val immunizationName: String?,

    @ColumnInfo(name = "status")
    val status: String?,

    @ColumnInfo(name = "disease_due_date")
    val diseaseDueDate: Instant?,

    @ColumnInfo(name = "recommendedVaccinations")
    val recommendedVaccinations: String?,
)
