package ca.bc.gov.bchealth.usecases.records

import androidx.work.ListenableWorker
import ca.bc.gov.common.exceptions.ProtectiveWordException
import ca.bc.gov.common.model.AuthParametersDto
import ca.bc.gov.common.model.ProtectiveWordState
import ca.bc.gov.common.model.relation.MedicationWithSummaryAndPharmacyDto
import ca.bc.gov.repository.MedicationRecordRepository
import ca.bc.gov.repository.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchMedicationsUseCase @Inject constructor(
    private val medicationRecordRepository: MedicationRecordRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : BaseRecordUseCase(dispatcher) {

    suspend fun execute(
        patientId: Long,
        authParameters: AuthParametersDto
    ): ListenableWorker.Result {
        return try {
            val medications = fetchMedicationResponse(authParameters)
            insertMedicationRecords(patientId, medications)
            ListenableWorker.Result.success()
        } catch (e: Exception) {
            when (e) {
                is ProtectiveWordException -> {
                    medicationRecordRepository.updateProtectiveWordState(ProtectiveWordState.PROTECTIVE_WORD_REQUIRED.value)
                    ListenableWorker.Result.success()
                }
                else -> {
                    e.printStackTrace()
                    ListenableWorker.Result.failure()
                }
            }
        }
    }

    private suspend fun fetchMedicationResponse(authParameters: AuthParametersDto): List<MedicationWithSummaryAndPharmacyDto> {
        var medications: List<MedicationWithSummaryAndPharmacyDto>
        withContext(dispatcher) {
            medications = medicationRecordRepository.fetchMedicationStatement(
                token = authParameters.token,
                hdid = authParameters.hdid,
            )
        }
        return medications
    }

    private suspend fun insertMedicationRecords(
        patientId: Long,
        medications: List<MedicationWithSummaryAndPharmacyDto>
    ) {
        medicationRecordRepository.updateMedicationRecords(medications, patientId)
    }
}
