package ca.bc.gov.data.datasource.local.entity.specialauthority

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import java.time.Instant

/*
* Created by amit_metri on 27,June,2022
*/
@Entity(
    tableName = "special_authority",
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
data class SpecialAuthorityEntity(
    // Defined own primary key("special_authority_id") because "referenceNumber" is set as nullable from the API specs
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "special_authority_id")
    val specialAuthorityId: Long = 0,

    @ColumnInfo(name = "patient_id")
    val patientId: Long,

    @ColumnInfo(name = "reference_number")
    val referenceNumber: String? = null,

    @ColumnInfo(name = "drug_name")
    val drugName: String? = null,

    @ColumnInfo(name = "request_status")
    val requestStatus: String? = null,

    @ColumnInfo(name = "prescriber_first_name")
    val prescriberFirstName: String? = null,

    @ColumnInfo(name = "prescriber_last_name")
    val prescriberLastName: String? = null,

    @ColumnInfo(name = "requested_date")
    val requestedDate: Instant? = null,

    @ColumnInfo(name = "effective_date")
    val effectiveDate: Instant? = null,

    @ColumnInfo(name = "expiry_date")
    val expiryDate: Instant? = null,

    @ColumnInfo(name = "data_source")
    val dataSource: DataSource = DataSource.BCSC
)
