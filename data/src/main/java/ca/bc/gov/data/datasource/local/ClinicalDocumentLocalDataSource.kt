package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.data.datasource.local.dao.ClinicalDocumentDao
import ca.bc.gov.data.model.mapper.toEntity
import javax.inject.Inject

class ClinicalDocumentLocalDataSource @Inject constructor(
    private val clinicalDocumentDao: ClinicalDocumentDao
) {

    suspend fun getClinicalDocument(clinicalDocumentId: Long) =
        clinicalDocumentDao.getClinicalDocumentDetails(clinicalDocumentId)

    suspend fun deleteClinicalDocuments(patientId: Long) =
        clinicalDocumentDao.delete(patientId)

    suspend fun insertClinicalDocuments(list: List<ClinicalDocumentDto>) =
        clinicalDocumentDao.insert(list.map { it.toEntity() })
}
