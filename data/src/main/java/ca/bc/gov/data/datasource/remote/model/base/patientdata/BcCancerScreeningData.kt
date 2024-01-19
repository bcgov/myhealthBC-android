package ca.bc.gov.data.datasource.remote.model.base.patientdata

/**
 * @author pinakin.kansara
 * Created 2024-01-18 at 10:27 a.m.
 */
data class BcCancerScreeningData(
    val id: String? = null,
    val resultDateTime: String?,
    val eventDateTime: String?,
    val fileId: String?,
    val programName: String?,
    val eventType: String?
) : PatientData(PatientDataType.BC_CANCER_SCREENING)
