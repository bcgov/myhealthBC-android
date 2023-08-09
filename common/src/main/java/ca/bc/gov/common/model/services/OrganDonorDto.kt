package ca.bc.gov.common.model.services

/**
 * @author Pinakin Kansara
 */
data class OrganDonorDto(
    var id: Long = 0,
    var patientId: Long = 0,
    val status: OrganDonorStatusDto = OrganDonorStatusDto.UNKNOWN,
    val statusMessage: String?,
    val registrationFileId: String?,
    var file: String?
) : PatientDataDto(type = PatientDataTypeDto.ORGAN_DONOR_REGISTRATION)
