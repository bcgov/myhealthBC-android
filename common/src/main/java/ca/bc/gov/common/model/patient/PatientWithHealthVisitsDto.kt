package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.healthvisits.HealthVisitsDto

/*
* Created by amit_metri on 21,June,2022
*/
data class PatientWithHealthVisitsDto(
    val patient: PatientDto,
    val healthVisits: List<HealthVisitsDto> = emptyList()
)
