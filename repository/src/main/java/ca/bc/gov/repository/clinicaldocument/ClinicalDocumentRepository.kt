package ca.bc.gov.repository.clinicaldocument

import ca.bc.gov.common.const.DATABASE_ERROR
import ca.bc.gov.common.exceptions.MyHealthException
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.data.datasource.local.ClinicalDocumentLocalDataSource
import ca.bc.gov.data.datasource.remote.ClinicalDocumentRemoteDataSource
import ca.bc.gov.data.model.mapper.toDto
import ca.bc.gov.repository.bcsc.BcscAuthRepo
import javax.inject.Inject

class ClinicalDocumentRepository @Inject constructor(
    private val localDataSource: ClinicalDocumentLocalDataSource,
    private val remoteDataSource: ClinicalDocumentRemoteDataSource,
    private val bcscAuthRepo: BcscAuthRepo
) {

    suspend fun fetchPdf(fileId: String): String {
        val authParameters = bcscAuthRepo.getAuthParametersDto()
        return remoteDataSource.fetchPdf(authParameters.token, authParameters.hdid, fileId)
    }

    suspend fun deleteClinicalDocuments(patientId: Long) =
        localDataSource.deleteClinicalDocuments(patientId)

    suspend fun insertClinicalDocuments(list: List<ClinicalDocumentDto>) =
        localDataSource.insertClinicalDocuments(list)

    suspend fun getClinicalDocuments(token: String, hdid: String) =
        remoteDataSource.getClinicalDocument(token, hdid)

    suspend fun getClinicalDocument(clinicalDocumentId: Long) =
        localDataSource.getClinicalDocument(clinicalDocumentId)?.toDto()
            ?: throw MyHealthException(
                DATABASE_ERROR, "No record found for clinicalDocumentId = $clinicalDocumentId"
            )
}
