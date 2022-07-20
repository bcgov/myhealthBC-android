package ca.bc.gov.data.datasource.local.entity.immunization

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "immunization_forecast",
    foreignKeys = [
        ForeignKey(
            entity = ImmunizationRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["immunization_record_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ImmunizationForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "immunization_record_id")
    val immunizationRecordId: Long,
    @ColumnInfo(name = "recommendation_id")
    val recommendationId: String? = null,
    @ColumnInfo(name = "create_date")
    val createDate: Instant,
    val status: String? = null,
    @ColumnInfo(name = "display_name")
    val displayName: String? = null,
    @ColumnInfo(name = "eligible_date")
    val eligibleDate: Instant,
    @ColumnInfo(name = "due_date")
    val dueDate: Instant
)
