package ca.bc.gov.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.common.model.ImmunizationStatus
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "vaccine_record",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VaccineRecordEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "patient_id")
    var patientId: Long,
    @ColumnInfo(name = "qr_issue_date")
    val qrIssueDate: Instant,
    val status: ImmunizationStatus,
    @ColumnInfo(name = "shc_uri")
    val shcUri: String,
    @ColumnInfo(name = "federal_pass")
    val federalPass: String?,
    @ColumnInfo(name = "data_source")
    val dataSource: DataSource
)
