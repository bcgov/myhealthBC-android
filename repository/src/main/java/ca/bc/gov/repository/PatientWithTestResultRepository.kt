package ca.bc.gov.repository

import ca.bc.gov.repository.testrecord.TestResultRepository
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientWithTestResultRepository @Inject constructor(
    private val testResultRepository: TestResultRepository
) {

    suspend fun deletePatientTestRecords(patientId: Long) =
        testResultRepository.deletePatientTestRecords(patientId)
}
