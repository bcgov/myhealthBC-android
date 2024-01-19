package ca.bc.gov.repository.services

import ca.bc.gov.common.model.services.PatientDataDto
import ca.bc.gov.data.datasource.remote.PatientServicesRemoteDataSource
import ca.bc.gov.data.datasource.remote.model.base.patientdata.PatientDataRequestType
import ca.bc.gov.repository.worker.MobileConfigRepository
import javax.inject.Inject

/**
 * @author Pinakin Kansara
 */
class PatientServicesRepository @Inject constructor(
    private val patientServicesRemoteDataSource: PatientServicesRemoteDataSource,
    private val mobileConfigRepository: MobileConfigRepository
) {
    suspend fun fetchPatientData(hdId: String): List<PatientDataDto> {
        val dataSetFeatureFlag = mobileConfigRepository.getPatientDataSetFeatureFlags()
        val organDonorFeatureFlag = mobileConfigRepository.getServicesFeatureFlag()
        val dataTypes = mutableListOf<String>()
        if (dataSetFeatureFlag.isDiagnosticImagingEnabled()) {
            dataTypes.add(PatientDataRequestType.DIAGNOSTIC_IMAGING.value)
        }
        if (organDonorFeatureFlag.isOrganDonorRegistrationEnabled()) {
            dataTypes.add(PatientDataRequestType.ORGAN_DONOR.value)
        }

        if (dataSetFeatureFlag.isBcCancerScreeningEnabled()) {
            dataTypes.add(PatientDataRequestType.BC_CANCER_SCREENING.value)
        }

        if (dataTypes.isEmpty()) {
            return emptyList()
        }
        return patientServicesRemoteDataSource.getPatientData(hdId, dataTypes)
    }

    suspend fun fetchPatientDataFile(hdid: String, fileId: String) =
        patientServicesRemoteDataSource.getPatientDataFile(hdid, fileId)
}
