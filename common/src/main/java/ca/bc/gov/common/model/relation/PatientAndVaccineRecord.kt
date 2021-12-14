package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.VaccineRecord
import ca.bc.gov.common.model.patient.Patient

/**
 * @author Pinakin Kansara
 */
data class PatientAndVaccineRecord(
    val patient: Patient,
    val vaccineRecord: VaccineRecord?
)
