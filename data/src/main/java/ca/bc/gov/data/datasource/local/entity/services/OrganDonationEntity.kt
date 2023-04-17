package ca.bc.gov.data.datasource.local.entity.services

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.bc.gov.common.model.services.OrganDonorStatusDto

@Entity(
    tableName = "organ_donation",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = ca.bc.gov.data.datasource.local.entity.PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = androidx.room.ForeignKey.CASCADE,
            onUpdate = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class OrganDonationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "patient_id")
    val patientId: Long,
    @ColumnInfo(defaultValue = "Unknown")
    val status: OrganDonorStatusDto = OrganDonorStatusDto.UNKNOWN,
    @ColumnInfo(name = "status_message")
    val statusMessage: String?,
    @ColumnInfo(name = "registration_file_id")
    val registrationFileId: String?,
    @ColumnInfo(name = "decision_file")
    val file: String?
)
