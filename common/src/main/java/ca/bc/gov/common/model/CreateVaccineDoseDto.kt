package ca.bc.gov.common.model

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class CreateVaccineDoseDto(
    val vaccineRecordId: Long,
    val productName: String,
    val providerName: String,
    val lotNumber: String,
    val date: Instant
)
