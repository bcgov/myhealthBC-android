package ca.bc.gov.data.datasource.remote.model.base.patientdata

/**
 * @author Pinakin Kansara
 */
data class DiagnosticImagingData(
    val id: String? = null,
    val examDate: String?,
    val fileId: String?,
    val examStatus: String?,
    val healthAuthority: String?,
    val organization: String?,
    val modality: String?,
    val bodyPart: String?,
    val procedureDescription: String?
) : PatientData(PatientDataType.DIAGNOSTIC_IMAGING_EXAM)
