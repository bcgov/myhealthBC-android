package ca.bc.gov.data.remote.model.base

data class Order(
    val additionalData: String?,
    val id: String?,
    val labResults: List<LabResult>?,
    val location: String?,
    val messageDateTime: String?,
    val messageId: String?,
    val orderingProviderIds: String?,
    val orderingProviders: String?,
    val ormOrOru: String?,
    val phn: String?,
    val reportAvailable: Boolean?,
    val reportingLab: String?
)
