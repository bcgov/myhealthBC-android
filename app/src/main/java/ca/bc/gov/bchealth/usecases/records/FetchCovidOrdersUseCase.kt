package ca.bc.gov.bchealth.usecases.records

import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.repository.RecordsRepository
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.testrecord.CovidOrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FetchCovidOrdersUseCase @Inject constructor(
    private val covidOrderRepository: CovidOrderRepository,
    private val recordsRepository: RecordsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto
    ) {
        val covidOrders = fetchRecord(authParameters, covidOrderRepository::fetchCovidOrders)
        recordsRepository.storeCovidOrders(patientId, covidOrders)
    }
}
