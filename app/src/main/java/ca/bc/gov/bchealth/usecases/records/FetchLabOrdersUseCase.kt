package ca.bc.gov.bchealth.usecases.records

import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.repository.RecordsRepository
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.labtest.LabOrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FetchLabOrdersUseCase @Inject constructor(
    private val labOrderRepository: LabOrderRepository,
    private val recordsRepository: RecordsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto
    ) {
        val labOrders = fetchRecord(authParameters, labOrderRepository::fetchLabOrders)
        recordsRepository.storeLabOrders(patientId, labOrders)
    }
}
