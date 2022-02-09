package ca.bc.gov.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.DataSource
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "vaccine_dose",
    foreignKeys = [
        ForeignKey(
            entity = VaccineRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["vaccine_record_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class VaccineDoseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "vaccine_record_id")
    var vaccineRecordId: Long,
    @ColumnInfo(name = "product_name")
    val productName: String?,
    @ColumnInfo(name = "provider_name")
    val providerName: String?,
    @ColumnInfo(name = "lot_number")
    val lotNumber: String?,
    val date: Instant,
    val dataSource: DataSource = DataSource.PUBLIC_API
)
