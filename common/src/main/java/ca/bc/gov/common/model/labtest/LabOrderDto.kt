package ca.bc.gov.common.model.labtest

import ca.bc.gov.common.model.DataSource
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class LabOrderDto(
    val id: Long = 0,
    var patientId: Long = 0,
    val labPdfId: String? = null,
    val reportId: String? = null,
    val collectionDateTime: Instant?,
    val timelineDateTime: Instant,
    val reportingSource: String? = null,
    val commonName: String? = null,
    val orderingProvider: String? = null,
    val testStatus: String? = null,
    val orderStatus: String? = null,
    val reportingAvailable: Boolean = false,
    val dataSorce: DataSource = DataSource.BCSC
)
