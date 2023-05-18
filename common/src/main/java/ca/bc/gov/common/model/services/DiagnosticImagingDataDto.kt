package ca.bc.gov.common.model.services

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class DiagnosticImagingDataDto(
    val _id: Long = 0,
    val id: String? = null,
    var patientId: Long = 0,
    val examDate: Instant?,
    val fileId: String?,
    val examStatus: DiagnosticImagingExamStatusDto = DiagnosticImagingExamStatusDto.UNKNOWN,
    val healthAuthority: String?,
    val organization: String?,
    val modality: String?,
    val bodyPart: String?,
    val procedureDescription: String?
) : PatientDataDto(type = PatientDataTypeDto.DIAGNOSTIC_IMAGING_EXAM)
