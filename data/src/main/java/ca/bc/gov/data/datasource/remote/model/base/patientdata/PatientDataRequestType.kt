package ca.bc.gov.data.datasource.remote.model.base.patientdata

enum class PatientDataRequestType(val value: String) {
    ORGAN_DONOR("OrganDonorRegistrationStatus"),
    DIAGNOSTIC_IMAGING("DiagnosticImaging")
}
