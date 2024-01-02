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
        val dataTypes = mutableListOf(PatientDataRequestType.ORGAN_DONOR.value)
        if (dataSetFeatureFlag.isDiagnosticImagingEnabled()) {
            dataTypes.add(PatientDataRequestType.DIAGNOSTIC_IMAGING.value)
        }
        return patientServicesRemoteDataSource.getPatientData(hdId, dataTypes)
    }

    suspend fun fetchPatientDataFile(hdid: String, fileId: String) =
        patientServicesRemoteDataSource.getPatientDataFile(hdid, fileId)
}
