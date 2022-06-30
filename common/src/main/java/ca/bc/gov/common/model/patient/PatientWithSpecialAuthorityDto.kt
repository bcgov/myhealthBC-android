package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.specialauthority.SpecialAuthorityDto

/*
* Created by amit_metri on 29,June,2022
*/
data class PatientWithSpecialAuthorityDto(
    val patient: PatientDto,
    val specialAuthorities: List<SpecialAuthorityDto> = emptyList()
)
