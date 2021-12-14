package ca.bc.gov.repository.model.mapper

import ca.bc.gov.common.model.CreateVaccineDoseDto
import ca.bc.gov.common.model.VaccineDose

fun VaccineDose.toCreateVaccineDoseDto(vaccineRecordId: Long) = CreateVaccineDoseDto(
    vaccineRecordId, productName, providerName, lotNumber, date
)