package ca.bc.gov.data.datasource.local.entity.immunization

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
    tableName = "immunization_record",
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
data class ImmunizationRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "patient_id")
    val patientId: Long,
    @ColumnInfo(name = "immunization_id")
    val immunizationId: String? = null,
    @ColumnInfo(name = "date_of_immunization")
    val dateOfImmunization: Instant,
    val status: String? = null,
    @ColumnInfo(name = "valid")
    val isValid: Boolean,
    @ColumnInfo(name = "provider_clinic")
    val providerOrClinic: String? = null,
    @ColumnInfo(name = "targeted_disease")
    val targetedDisease: String? = null,
    @ColumnInfo(name = "immunization_name")
    val immunizationName: String? = null,
    @ColumnInfo(name = "agent_code")
    val agentCode: String? = null,
    @ColumnInfo(name = "agent_name")
    val agentName: String? = null,
    val lotNumber: String? = null,
    val productName: String? = null,
    @ColumnInfo(name = "data_source")
    val dataSource: DataSource = DataSource.BCSC

)
