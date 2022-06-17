package ca.bc.gov.common.model.test

import ca.bc.gov.common.model.DataSource
import java.time.Instant

/**
 * @author Pinakin Kansara
 */
data class CovidOrderDto(
    val id: String,
    val phn: String?,
    val orderingProviderIds: String?,
    val orderingProviders: String?,
    val reportingLab: String?,
    val location: String?,
    val ormOrOru: String?,
    val messageDateTime: Instant,
    val messageId: String?,
    val additionalData: String?,
    val reportAvailable: Boolean,
    var patientId: Long = 0,
    val dataSource: DataSource = DataSource.BCSC
)
