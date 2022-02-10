package ca.bc.gov.common.model.relation

import ca.bc.gov.common.model.VaccineDoseDto
import ca.bc.gov.common.model.VaccineRecordDto

/**
 * @author Pinakin Kansara
 */
data class VaccineWithDosesDto(
    val vaccine: VaccineRecordDto,
    val doses: List<VaccineDoseDto> = emptyList()
)
