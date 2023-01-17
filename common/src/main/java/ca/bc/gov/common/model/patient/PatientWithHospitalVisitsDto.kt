package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.hospitalvisits.HospitalVisitDto

data class PatientWithHospitalVisitsDto(
    val patient: PatientDto,
    val hospitalVisits: List<HospitalVisitDto> = emptyList()
)
