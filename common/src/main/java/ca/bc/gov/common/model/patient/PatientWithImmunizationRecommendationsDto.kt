package ca.bc.gov.common.model.patient

import ca.bc.gov.common.model.immunization.ImmunizationRecommendationsDto

/**
 * @author Pinakin Kansara
 */
data class PatientWithImmunizationRecommendationsDto(
    val patient: PatientDto,
    val recommendations: List<ImmunizationRecommendationsDto>
)
