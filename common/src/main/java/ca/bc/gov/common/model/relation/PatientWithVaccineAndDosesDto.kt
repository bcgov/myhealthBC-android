package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.patient.PatientDto

/**
 * @author Pinakin Kansara
 */
data class PatientWithVaccineAndDosesDto(
    val patient: PatientDto,
    val vaccineWithDoses: VaccineWithDosesDto?
)
