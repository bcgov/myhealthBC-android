package ca.bc.gov.data.datasource.local.entity.labtest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "lab_order",
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
data class LabOrderEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "patient_id")
    val patientId: Long,
    @ColumnInfo(name = "report_id")
    val reportId: String? = null,
    @ColumnInfo(name = "collection_date_time")
    val collectionDateTime: Instant,
    @ColumnInfo(name = "reporting_source")
    val reportingSource: String? = null,
    @ColumnInfo(name = "common_name")
    val commonName: String? = null,
    @ColumnInfo(name = "ordering_provider")
    val orderingProvider: String? = null,
    @ColumnInfo(name = "test_status")
    val testStatus: String? = null,
    @ColumnInfo(name = "report_available")
    val reportAvailable: Boolean = false,
    @ColumnInfo(name = "data_source")
    val dataSource: DataSource = DataSource.BCSC
)
