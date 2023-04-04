package ca.bc.gov.bchealth.usecases.records

import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto
import ca.bc.gov.repository.RecordsRepository
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.hospitalvisit.HospitalVisitRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FetchHospitalVisitsUseCase @Inject constructor(
    private val hospitalVisitRepository: HospitalVisitRepository,
    private val recordsRepository: RecordsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto
    ) {
        val hospitalVisits: List<HospitalVisitDto>? = fetchRecord(
            authParameters, hospitalVisitRepository::getHospitalVisits
        )

        hospitalVisits?.let {
            recordsRepository.storeHospitalVisits(patientId, it)
        }
    }
}
