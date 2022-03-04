package ca.bc.gov.data.datasource.remote.model.base.medication

data class MedicationStatementPayload(
    val dateEntered: String?,
    val directions: String?,
    val dispensedDate: String,
    val dispensingPharmacy: DispensingPharmacy?,
    val medicationSummary: MedicationSummary?,
    val pharmacyId: String?,
    val practitionerSurname: String?,
    val prescriptionIdentifier: String?,
    val prescriptionStatus: String?
)
