package ca.bc.gov.data.datasource.remote.model.base.patientdata

/**
 * @author Pinakin Kansara
 * This data class combines field used for Organ Donor & DiagnosticImaging.
 */
data class PatientDataItem(
    /**
     * type can be either
     * Organ Donor
     * Or
     * DiagnosticImaging
     */
    val type: String,
    val status: OrganDonorStatus = OrganDonorStatus.UNKNOWN,
    val statusMessage: String?,
    val registrationFileId: String?,
)
