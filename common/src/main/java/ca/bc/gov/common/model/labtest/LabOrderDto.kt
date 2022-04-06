package ca.bc.gov.common.model.labtest

import ca.bc.gov.common.model.DataSource
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class LabOrderDto(
    val id: String,
    var patientId: Long = 0,
    val reportId: String? = null,
    val collectionDateTime: Instant,
    val timelineDateTime: Instant,
    val reportingSource: String? = null,
    val commonName: String? = null,
    val orderingProvider: String? = null,
    val testStatus: String? = null,
    val reportingAvailable: Boolean = false,
    val dataSorce: DataSource = DataSource.BCSC
)
