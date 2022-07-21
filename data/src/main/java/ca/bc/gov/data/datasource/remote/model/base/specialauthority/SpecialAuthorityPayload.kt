package ca.bc.gov.data.datasource.remote.model.base.specialauthority

/**
 * @author: Created by Rashmi Bambhania on 24,June,2022
 */
data class SpecialAuthorityPayload(
    val referenceNumber: String? = null,
    val drugName: String? = null,
    val effectiveDate: String? = null,
    val expiryDate: String? = null,
    val prescriberFirstName: String? = null,
    val prescriberLastName: String? = null,
    val requestStatus: String? = null,
    val requestedDate: String? = null
)
