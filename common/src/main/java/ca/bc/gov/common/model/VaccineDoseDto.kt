package ca.bc.gov.common.model

import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class VaccineDoseDto(
    val id: Long = 0,
    var vaccineRecordId: Long = 0,
    var productName: String?,
    val providerName: String?,
    val lotNumber: String?,
    val date: Instant
)
