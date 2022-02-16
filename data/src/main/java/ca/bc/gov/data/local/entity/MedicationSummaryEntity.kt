package ca.bc.gov.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "medication_summary",
    foreignKeys = [
        ForeignKey(
            entity = MedicationRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["medication_record_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class MedicationSummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "medication_record_id")
    val medicationRecordId: Long,
    val din: Long,
    @ColumnInfo(name = "brand_name")
    val brandName: String,
    @ColumnInfo(name = "generic_name")
    val genericName: String,
    @ColumnInfo(name = "quantity")
    val quantity: Int,
    @ColumnInfo(name = "max_daily_dosage")
    val maxDailyDosage: Int,
    @ColumnInfo(name = "drug_discontinue_date")
    val drugDiscontinueDate: Instant,
    val form: String,
    val manufacturer: String,
    val strength: String,
    @ColumnInfo(name = "strength_unit")
    val strengthUnit: String,
    @ColumnInfo(name = "is_pin")
    val isPin: Boolean
)
