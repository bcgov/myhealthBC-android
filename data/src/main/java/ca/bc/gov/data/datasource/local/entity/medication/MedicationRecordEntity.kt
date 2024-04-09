package ca.bc.gov.data.datasource.local.entity.medication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.DataSource
import ca.bc.gov.data.datasource.local.entity.PatientEntity

/**
 * @author Pinakin Kansara
 */
@Entity(
    tableName = "medication_record",
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
data class MedicationRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "patient_id")
    val patientId: Long,
    @ColumnInfo(name = "prescription_identifier")
    val prescriptionIdentifier: String?,
    @ColumnInfo(name = "prescription_status")
    val prescriptionStatus: String?,
    @ColumnInfo(name = "practitioner_surname")
    val practitionerSurname: String?,
    @ColumnInfo(name = "dispense_date")
    val dispenseDate: String,
    val directions: String?,
    @ColumnInfo(name = "data_source")
    val dataSource: DataSource
)
