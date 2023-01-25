package ca.bc.gov.data.datasource.local.entity.clinicaldocument

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import java.time.Instant

@Entity(
    tableName = "clinical_documents",
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
data class ClinicalDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "clinical_document_id")
    val clinicalDocumentId: Long = 0,

    @ColumnInfo(name = "patient_id")
    val patientId: Long,

    @ColumnInfo(name = "fileId")
    val fileId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "facilityName")
    val facilityName: String,

    @ColumnInfo(name = "discipline")
    val discipline: String,

    @ColumnInfo(name = "serviceDate")
    val serviceDate: Instant,
)
