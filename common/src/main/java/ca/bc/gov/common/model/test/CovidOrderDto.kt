package ca.bc.gov.common.model.test

import ca.bc.gov.common.model.DataSource

/**
 * @author Pinakin Kansara
 */
data class CovidOrderDto(
    val id: Long = 0,
    val covidOrderId: String,
    val phn: String?,
    val orderingProviderIds: String?,
    val orderingProviders: String?,
    val reportingLab: String?,
    val location: String?,
    val ormOrOru: String?,
    val messageId: String?,
    val additionalData: String?,
    val reportAvailable: Boolean,
    var patientId: Long = 0,
    val dataSource: DataSource = DataSource.BCSC
)
