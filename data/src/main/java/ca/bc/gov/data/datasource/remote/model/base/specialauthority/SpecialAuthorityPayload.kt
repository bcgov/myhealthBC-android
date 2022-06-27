package ca.bc.gov.data.datasource.remote.model.base.specialauthority

import java.time.Instant

/**
 * @author: Created by Rashmi Bambhania on 24,June,2022
 */
data class SpecialAuthorityPayload(
    val drugName: String? = null,
    val effectiveDate: Instant? = null,
    val expiryDate: Instant? = null,
    val prescriberFirstName: String? = null,
    val prescriberLastName: String? = null,
    val referenceNumber: String? = null,
    val requestStatus: String? = null,
    val requestedDate: Instant? = null
)
