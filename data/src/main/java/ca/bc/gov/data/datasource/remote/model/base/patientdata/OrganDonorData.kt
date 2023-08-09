package ca.bc.gov.data.datasource.remote.model.base.patientdata

import ca.bc.gov.data.datasource.remote.model.base.patientdata.organdonor.OrganDonorStatus

data class OrganDonorData(
    val id: String? = null,
    val status: OrganDonorStatus = OrganDonorStatus.UNKNOWN,
    val statusMessage: String?,
    val registrationFileId: String?
) : PatientData(PatientDataType.ORGAN_DONOR_REGISTRATION)
