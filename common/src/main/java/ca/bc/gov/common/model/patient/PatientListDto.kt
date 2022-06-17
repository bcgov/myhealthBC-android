package ca.bc.gov.common.model.patient

/*
* Created by amit_metri on 10,February,2022
*/
data class PatientListDto(
    val patientDtos: List<PatientDto> = emptyList()
)
