package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.dependents.DependentDto

data class PatientWithDependentAndListOrderDto(
    val patient: PatientDto,
    val dependents: List<DependentDto>
)
