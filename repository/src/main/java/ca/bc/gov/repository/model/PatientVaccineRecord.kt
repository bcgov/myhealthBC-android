package ca.bc.gov.repository.model

import ca.bc.gov.common.model.VaccineRecord
import ca.bc.gov.common.model.patient.Patient

/**
 * @author Pinakin Kansara
 */
data class PatientVaccineRecord(
    val patient: Patient,
    val vaccineRecord: VaccineRecord
)
