package ca.bc.gov.data.datasource.local.entity.covid

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "covid_test",
    foreignKeys = [
        ForeignKey(
            entity = CovidOrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["covid_order_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class CovidTestEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "covid_order_id")
    val covidOrderId: String,
    @ColumnInfo(name = "test_type")
    val testType: String?,
    @ColumnInfo(name = "out_of_range")
    val outOfRange: Boolean,
    @ColumnInfo(name = "collected_date_time")
    val collectedDateTime: Instant,
    @ColumnInfo(name = "test_status")
    val testStatus: String?,
    @ColumnInfo(name = "lab_result_outcome")
    val labResultOutcome: String?,
    @ColumnInfo(name = "result_description")
    val resultDescription: String?,
    @ColumnInfo(name = "result_link")
    val resultLink: String?,
    @ColumnInfo(name = "received_date_time")
    val receivedDateTime: Instant,
    @ColumnInfo(name = "result_date_time")
    val resultDateTime: Instant,
    val loinc: String?,
    @ColumnInfo(name = "loinc_name")
    val loincName: String?

)
