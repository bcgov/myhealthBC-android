package ca.bc.gov.common.model.specialauthority

import ca.bc.gov.common.model.DataSource
import java.time.Instant

/**
 * @author: Created by Rashmi Bambhania on 24,June,2022
 */
class SpecialAuthorityDto(
    val specialAuthorityId: Long = 0,
    var patientId: Long,
    val referenceNumber: String? = null,
    val drugName: String? = null,
    val requestStatus: String? = null,
    val prescriberFirstName: String? = null,
    val prescriberLastName: String? = null,
    val requestedDate: Instant? = null,
    val effectiveDate: Instant? = null,
    val expiryDate: Instant? = null,
    val dataSource: DataSource = DataSource.BCSC
)
