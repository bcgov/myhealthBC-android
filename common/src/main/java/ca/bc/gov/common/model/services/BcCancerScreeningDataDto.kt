package ca.bc.gov.common.model.services

/**
 * @author pinakin.kansara
 * Created 2024-01-18 at 10:30 a.m.
 */
data class BcCancerScreeningDataDto(
    val _id: Long = 0,
    val id: String? = null,
    var patientId: Long = 0,
    val resultDateTime: String?,
    val eventDateTime: String?,
    val fileId: String?,
    val programName: String?,
    val eventType: String?
) : PatientDataDto(PatientDataTypeDto.BC_CANCER_SCREENING)
