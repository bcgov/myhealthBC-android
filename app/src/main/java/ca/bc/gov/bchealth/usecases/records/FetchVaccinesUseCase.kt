package ca.bc.gov.bchealth.usecases.records

import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.dependents.DependentDto
import ca.bc.gov.repository.DependentsRepository
import ca.bc.gov.repository.FetchVaccineRecordRepository
import ca.bc.gov.repository.RecordsRepository
import ca.bc.gov.repository.di.IoDispatcher
import ca.bc.gov.repository.model.PatientVaccineRecord
import ca.bc.gov.repository.model.PatientVaccineRecordsState
import ca.bc.gov.repository.qr.VaccineRecordState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchVaccinesUseCase @Inject constructor(
    private val fetchVaccineRecordRepository: FetchVaccineRecordRepository,
    private val recordsRepository: RecordsRepository,
    private val dependentsRepository: DependentsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto,
        dependents: List<DependentDto>?
    ) {
        val vaccineRecords = mutableListOf<PatientVaccineRecordsState?>()

        val patientVaccineRecords = fetchVaccineRecords(
            authParameters.token,
            authParameters.hdid,
            patientId
        )
        vaccineRecords.add(patientVaccineRecords)

        val dependentVaccineRecords = fetchDependentsVaccineRecords(
            authParameters.token, dependents
        )

        vaccineRecords.addAll(dependentVaccineRecords)
        recordsRepository.storeVaccineRecords(vaccineRecords)
    }

    private suspend fun fetchVaccineRecords(
        token: String,
        hdid: String,
        patientId: Long
    ): PatientVaccineRecordsState? {
        var response: Pair<VaccineRecordState, PatientVaccineRecord?>?
        withContext(dispatcher) {
            response = fetchVaccineRecordRepository.fetchVaccineRecord(token, hdid)
        }
        return response?.let {
            PatientVaccineRecordsState(
                patientId = patientId,
                vaccineRecordState = it.first,
                patientVaccineRecord = it.second
            )
        }
    }

    private suspend fun fetchDependentsVaccineRecords(
        token: String,
        dependents: List<DependentDto>?
    ): List<PatientVaccineRecordsState> {
        val resultList = mutableListOf<PatientVaccineRecordsState>()

        dependents?.forEach { dependent ->
            try {
                val patientId = dependentsRepository.getDependentByPhn(dependent.phn).patientId

                fetchVaccineRecords(
                    token,
                    dependent.hdid,
                    patientId
                )?.let { dependentVaccineRecord ->
                    resultList.add(dependentVaccineRecord)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return resultList
    }
}
