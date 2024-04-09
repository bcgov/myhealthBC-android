package ca.bc.gov.common.model.services

/**
 * @author Pinakin Kansara
 */
data class DiagnosticImagingDataDto(
    val _id: Long = 0,
    val id: String? = null,
    val isUpdated: Boolean,
    var patientId: Long = 0,
    val examDate: String?,
    val fileId: String?,
    val examStatus: String,
    val healthAuthority: String?,
    val organization: String?,
    val modality: String?,
    val bodyPart: String?,
    val procedureDescription: String?
) : PatientDataDto(type = PatientDataTypeDto.DIAGNOSTIC_IMAGING_EXAM)
