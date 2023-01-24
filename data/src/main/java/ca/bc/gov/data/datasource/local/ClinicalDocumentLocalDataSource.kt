package ca.bc.gov.data.datasource.local

import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import javax.inject.Inject

class ClinicalDocumentLocalDataSource @Inject constructor(
    //private val clinicalDocumentsDao: ClinicalDocumentsDao
) {

    suspend fun getClinicalDocuments(clinicalDocumentId: Long) = {}
    //hospitalVisitDao.getHospitalVisitDetails(clinicalDocumentId)

    suspend fun deleteClinicalDocuments(patientId: Long) = {}
    //hospitalVisitDao.delete(patientId)

    suspend fun insertClinicalDocuments(list: List<ClinicalDocumentDto>) = {}
    //hospitalVisitDao.insert(list.map { it.toEntity() })
}
