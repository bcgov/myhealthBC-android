package ca.bc.gov.data.datasource.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import ca.bc.gov.data.datasource.local.entity.PatientEntity
import ca.bc.gov.data.datasource.local.entity.clinicaldocument.ClinicalDocumentEntity

data class PatientWithClinicalDocuments(
    @Embedded
    val patient: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patient_id"
    )
    val clinicalDocuments: List<ClinicalDocumentEntity> = emptyList()
)
