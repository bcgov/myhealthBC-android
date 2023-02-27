package ca.bc.gov.data.datasource.remote.model.base

data class CovidOrder(
    val id: String,
    val phn: String?,
    val orderingProviderIds: String?,
    val orderingProviders: String?,
    val reportingLab: String?,
    val location: String?,
    val ormOrOru: String?,
    val messageDateTime: String,
    val messageId: String?,
    val additionalData: String?,
    val reportAvailable: Boolean,
    val labResults: List<CovidLabResult> = emptyList()
)
