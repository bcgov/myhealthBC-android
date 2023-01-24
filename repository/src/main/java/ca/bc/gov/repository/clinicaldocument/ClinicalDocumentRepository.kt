package ca.bc.gov.repository.clinicaldocument

import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.data.datasource.local.ClinicalDocumentLocalDataSource
import ca.bc.gov.data.datasource.remote.ClinicalDocumentRemoteDataSource
import javax.inject.Inject

class ClinicalDocumentRepository @Inject constructor(
    private val localDataSource: ClinicalDocumentLocalDataSource,
    private val remoteDataSource: ClinicalDocumentRemoteDataSource
) {

    suspend fun deleteClinicalDocuments(patientId: Long) =
        localDataSource.deleteClinicalDocuments(patientId)

    suspend fun insertClinicalDocuments(list: List<ClinicalDocumentDto>) =
        localDataSource.insertClinicalDocuments(list)

    suspend fun getClinicalDocuments(token: String, hdid: String) =
        remoteDataSource.getClinicalDocument(token, hdid)
}