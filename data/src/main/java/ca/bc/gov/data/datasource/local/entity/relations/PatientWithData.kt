package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.services.DiagnosticImagingDataEntity

/**
 * @author Pinakin Kansara
 */
data class PatientWithData(
    @Embedded
    val patient: PatientEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id"
    )
    val diagnosticImagingDataList: List<DiagnosticImagingDataEntity> = emptyList()
)
