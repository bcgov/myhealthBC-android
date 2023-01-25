package ca.bc.gov.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Query
import ca.bc.gov.data.datasource.local.entity.clinicaldocument.ClinicalDocumentEntity

@Dao
interface ClinicalDocumentDao : BaseDao<ClinicalDocumentEntity> {
    @Query("DELETE FROM clinical_documents WHERE patient_id = :patientId")
    suspend fun delete(patientId: Long): Int

    @Query("SELECT * FROM clinical_documents WHERE clinical_document_id = :id")
    suspend fun getClinicalDocumentDetails(id: Long): ClinicalDocumentEntity?
}
