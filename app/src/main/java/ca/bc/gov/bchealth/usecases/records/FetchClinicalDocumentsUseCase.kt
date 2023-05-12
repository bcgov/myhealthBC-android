package ca.bc.gov.bchealth.usecases.records

import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.clinicaldocument.ClinicalDocumentDto
import ca.bc.gov.repository.RecordsRepository
import ca.bc.gov.repository.clinicaldocument.ClinicalDocumentRepository
import ca.bc.gov.repository.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FetchClinicalDocumentsUseCase @Inject constructor(
    private val clinicalDocumentRepository: ClinicalDocumentRepository,
    private val recordsRepository: RecordsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto
    ) {
        val clinicalDocuments: List<ClinicalDocumentDto>? = fetchRecord(
            authParameters, clinicalDocumentRepository::getClinicalDocuments
        )

        clinicalDocuments?.let {
            recordsRepository.storeClinicalDocuments(patientId, it)
        }
    }
}
