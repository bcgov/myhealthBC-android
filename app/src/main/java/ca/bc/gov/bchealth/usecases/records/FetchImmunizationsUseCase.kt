package ca.bc.gov.bchealth.usecases.records

import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.repository.RecordsRepository
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.immunization.ImmunizationRecordRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FetchImmunizationsUseCase @Inject constructor(
    private val immunizationRecordRepository: ImmunizationRecordRepository,
    private val recordsRepository: RecordsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto
    ) {
        val immunizations = fetchRecord(
            authParameters, immunizationRecordRepository::fetchImmunization
        )

        recordsRepository.storeImmunizationRecords(patientId, immunizations)
    }
}
