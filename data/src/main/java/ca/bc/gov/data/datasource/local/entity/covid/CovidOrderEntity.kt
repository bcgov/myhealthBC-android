package ca.bc.gov.data.datasource.local.entity.covid

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
    tableName = "covid_order",
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
data class CovidOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "covid_order_id")
    val covidOrderId: String,
    @ColumnInfo(name = "patient_id")
    val patientId: Long,
    val phn: String?,
    @ColumnInfo(name = "ordering_provider_id")
    val orderingProviderIds: String?,
    @ColumnInfo(name = "ordering_providers")
    val orderingProviders: String?,
    @ColumnInfo(name = "reporting_lab")
    val reportingLab: String?,
    val location: String?,
    @ColumnInfo(name = "orm_or_oru")
    val ormOrOru: String?,
    @ColumnInfo(name = "message_date_time")
    val messageDateTime: Instant,
    @ColumnInfo(name = "message_id")
    val messageId: String?,
    @ColumnInfo(name = "additional_data")
    val additionalData: String?,
    @ColumnInfo(name = "report_available")
    val reportAvailable: Boolean,
    @ColumnInfo(name = "data_source")
    val dataSource: DataSource = DataSource.BCSC
)