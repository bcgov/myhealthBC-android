package ca.bc.gov.bchealth.usecases.records

import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.healthvisits.HealthVisitsDto
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.healthvisits.HealthVisitsRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FetchHealthVisitsUseCase @Inject constructor(
    private val healthVisitsRepository: HealthVisitsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto
    ) {
        val healthVisits: List<HealthVisitsDto>? = fetchRecord(
            authParameters, healthVisitsRepository::getHealthVisits
        )
        insertHealthVisits(patientId, healthVisits)
    }

    private suspend fun insertHealthVisits(
        patientId: Long,
        healthVisits: List<HealthVisitsDto>?
    ) {
        healthVisitsRepository.deleteHealthVisits(patientId)
        healthVisits?.let { list ->
            list.forEach {
                it.patientId = patientId
            }
            healthVisitsRepository.insert(list)
        }
    }
}
