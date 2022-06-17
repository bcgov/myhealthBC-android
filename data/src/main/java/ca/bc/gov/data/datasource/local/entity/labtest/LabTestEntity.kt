package ca.bc.gov.data.datasource.local.entity.labtest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "lab_test",
    foreignKeys = [
        ForeignKey(
            entity = LabOrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["lab_order_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class LabTestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "lab_order_id")
    val labOrderId: Long,
    @ColumnInfo(name = "obx_id")
    val obxId: String? = null,
    @ColumnInfo(name = "batter_type")
    val batteryType: String? = null,
    @ColumnInfo(name = "out_of_range")
    val outOfRange: Boolean = false,
    val loinc: String? = null,
    @ColumnInfo(name = "test_status")
    val testStatus: String? = null
)
